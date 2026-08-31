/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.github.rubensousa.macgyver.stream

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.meta.wearable.dat.camera.Camera
import com.meta.wearable.dat.camera.Stream
import com.meta.wearable.dat.camera.addCamera
import com.meta.wearable.dat.camera.removeCamera
import com.meta.wearable.dat.camera.types.PhotoData
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.StreamState
import com.meta.wearable.dat.camera.types.VideoFrame
import com.meta.wearable.dat.camera.types.VideoQuality
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.DeviceSelector
import com.meta.wearable.dat.core.session.DeviceSession
import com.meta.wearable.dat.core.session.DeviceSessionState
import io.github.rubensousa.macgyver.phone.PhoneCameraManager
import io.github.rubensousa.macgyver.wearables.WearablesViewModel
import io.github.rubensousa.macgyver.webrtc.WebRTCSessionViewModel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * DAT 0.9 may deliver compressed content or codec configuration alongside raw video. Only a
 * complete, contiguous I420 payload is allowed to enter an app raw-frame path.
 */
internal fun isVerifiedContiguousRawI420(
    width: Int,
    height: Int,
    isCompressed: Boolean,
    isCodecConfig: Boolean,
    bufferRemaining: Int,
): Boolean {
  val expectedI420Bytes = width.toLong() * height * 3 / 2
  return !isCompressed &&
      !isCodecConfig &&
      expectedI420Bytes in 1..Int.MAX_VALUE &&
      bufferRemaining.toLong() == expectedI420Bytes
}

class StreamViewModel(
    application: Application,
    private val wearablesViewModel: WearablesViewModel,
) : AndroidViewModel(application) {

  companion object {
    private const val TAG = "StreamViewModel"
    private val INITIAL_STATE = StreamUiState()
  }

  private val deviceSelector: DeviceSelector = wearablesViewModel.deviceSelector
  private var deviceSession: DeviceSession? = null
  private var camera: Camera? = null
  private var stream: Stream? = null

  private val _uiState = MutableStateFlow(INITIAL_STATE)
  val uiState: StateFlow<StreamUiState> = _uiState.asStateFlow()

  private var videoJob: Job? = null
  private var stateJob: Job? = null
  private var sessionStateJob: Job? = null
  private var cameraSetupJob: Job? = null
  private var foregroundServiceStarted = false

  // macgyver additions
  var webrtcViewModel: WebRTCSessionViewModel? = null
  private var phoneCameraManager: PhoneCameraManager? = null

  fun startStream() {
    videoJob?.cancel()
    stateJob?.cancel()
    sessionStateJob?.cancel()
    cameraSetupJob?.cancel()

    _uiState.update { it.copy(streamingMode = StreamingMode.GLASSES) }
    Wearables.createSession(deviceSelector)
        .onSuccess { created ->
          deviceSession = created
          sessionStateJob = viewModelScope.launch {
            created.state.collect { if (it == DeviceSessionState.STOPPED) stopStream() }
          }
          created.start()
          cameraSetupJob =
              viewModelScope.launch {
                val sessionState =
                    created.state.first {
                      it == DeviceSessionState.STARTED || it == DeviceSessionState.STOPPED
                    }
                if (deviceSession !== created) return@launch
                if (sessionState != DeviceSessionState.STARTED) {
                  Log.e(TAG, "DAT session stopped before camera setup")
                  stopStream()
                  return@launch
                }
                created
                    .addCamera(
                        StreamConfiguration(
                            videoQuality = VideoQuality.MEDIUM,
                            frameRate = 24,
                            compressVideo = false,
                        )
                    )
                    .onSuccess { addedCamera ->
                      camera = addedCamera
                      stream = addedCamera.stream
                      videoJob =
                          viewModelScope.launch(Dispatchers.Default) {
                            addedCamera.stream.videoStream.collect { handleVideoFrame(it) }
                          }
                      stateJob =
                          viewModelScope.launch {
                            addedCamera.stream.state.collect { currentState ->
                              _uiState.update { it.copy(streamSessionState = currentState) }
                              if (currentState == StreamState.STREAMING) {
                                startForegroundService()
                              }
                              if (
                                  currentState == StreamState.STOPPED ||
                                      currentState == StreamState.CLOSED
                              ) {
                                wearablesViewModel.navigateToDeviceSelection()
                              }
                            }
                          }
                      addedCamera.stream.start().onFailure { error, _ ->
                        Log.e(TAG, "Failed to start DAT camera stream: $error")
                        stopStream()
                      }
                    }
                    .onFailure { error, _ ->
                      Log.e(TAG, "Failed to add DAT camera: $error")
                      stopStream()
                    }
                  }
        }
        .onFailure { error, _ ->
          Log.e(TAG, "Failed to create DAT session: $error")
          stopStream()
        }
  }

  fun startPhoneCamera(lifecycleOwner: LifecycleOwner) {
    val manager = PhoneCameraManager(getApplication())
    phoneCameraManager = manager

    manager.onFrameCaptured = { bitmap ->
      _uiState.update { it.copy(videoFrame = bitmap) }
      // Forward to WebRTC (every frame)
      webrtcViewModel?.pushVideoFrame(bitmap)
    }

    _uiState.update {
      it.copy(
        streamingMode = StreamingMode.PHONE,
        streamSessionState = StreamState.STREAMING,
      )
    }
    manager.start(lifecycleOwner)
    Log.d(TAG, "Phone camera mode started")
  }

  fun stopStream() {
    if (foregroundServiceStarted) {
      StreamingService.stop(getApplication())
      foregroundServiceStarted = false
    }

    videoJob?.cancel()
    videoJob = null
    stateJob?.cancel()
    stateJob = null
    sessionStateJob?.cancel()
    sessionStateJob = null
    cameraSetupJob?.cancel()
    cameraSetupJob = null

    // A Camera owns its child Stream in DAT 0.9. Stop it before detaching the
    // capability from the session, then discard the terminal session.
    val activeCamera = camera
    val activeSession = deviceSession
    stream = null
    camera = null
    deviceSession = null
    activeCamera?.stop()
    activeSession?.removeCamera()
    activeSession?.stop()
    phoneCameraManager?.stop()
    phoneCameraManager = null
    _uiState.update { INITIAL_STATE }
  }

  private fun startForegroundService() {
    if (!foregroundServiceStarted) {
      foregroundServiceStarted = true
      StreamingService.start(getApplication())
    }
  }

  fun capturePhoto() {
    if (uiState.value.isCapturing) {
      Log.d(TAG, "Photo capture already in progress, ignoring request")
      return
    }

    if (uiState.value.streamSessionState == StreamState.STREAMING) {
      // Phone mode: capture current video frame as photo
      if (uiState.value.streamingMode == StreamingMode.PHONE) {
        uiState.value.videoFrame?.let { frame ->
          _uiState.update { it.copy(capturedPhoto = frame, isShareDialogVisible = true) }
        }
        return
      }

      Log.d(TAG, "Starting photo capture")
      _uiState.update { it.copy(isCapturing = true) }

      viewModelScope.launch {
        stream
            ?.capturePhoto()
            ?.onSuccess { photoData ->
              Log.d(TAG, "Photo capture successful")
              handlePhotoData(photoData)
              _uiState.update { it.copy(isCapturing = false) }
            }
            ?.onFailure {
              Log.e(TAG, "Photo capture failed")
              _uiState.update { it.copy(isCapturing = false) }
            }
      }
    } else {
      Log.w(
          TAG,
          "Cannot capture photo: stream not active (state=${uiState.value.streamSessionState})",
      )
    }
  }

  fun showShareDialog() {
    _uiState.update { it.copy(isShareDialogVisible = true) }
  }

  fun hideShareDialog() {
    _uiState.update { it.copy(isShareDialogVisible = false) }
  }

  fun sharePhoto(bitmap: Bitmap) {
    val context = getApplication<Application>()
    val imagesFolder = File(context.cacheDir, "images")
    try {
      imagesFolder.mkdirs()
      val file = File(imagesFolder, "shared_image.png")
      FileOutputStream(file).use { stream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
      }

      val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
      val intent = Intent(Intent.ACTION_SEND)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      intent.putExtra(Intent.EXTRA_STREAM, uri)
      intent.type = "image/png"
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

      val chooser = Intent.createChooser(intent, "Share Image")
      chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      context.startActivity(chooser)
    } catch (e: IOException) {
      Log.e("StreamViewModel", "Failed to share photo", e)
    }
  }

  private fun handleVideoFrame(videoFrame: VideoFrame) {
    if (
        !isVerifiedContiguousRawI420(
            width = videoFrame.width,
            height = videoFrame.height,
            isCompressed = videoFrame.isCompressed,
            isCodecConfig = videoFrame.isCodecConfig,
            bufferRemaining = videoFrame.buffer.remaining(),
        )
    ) {
      Log.w(TAG, "Rejected non-raw camera frame")
      return
    }
    // VideoFrame contains raw I420 video data in a ByteBuffer
    val buffer = videoFrame.buffer
    val dataSize = buffer.remaining()
    val byteArray = ByteArray(dataSize)

    // Save current position
    val originalPosition = buffer.position()
    buffer.get(byteArray)
    // Restore position
    buffer.position(originalPosition)

    // Convert I420 to NV21 format which is supported by Android's YuvImage
    val nv21 = convertI420toNV21(byteArray, videoFrame.width, videoFrame.height)
    val image = YuvImage(nv21, ImageFormat.NV21, videoFrame.width, videoFrame.height, null)
    val out =
        ByteArrayOutputStream().use { stream ->
          image.compressToJpeg(Rect(0, 0, videoFrame.width, videoFrame.height), 50, stream)
          stream.toByteArray()
        }

    val bitmap = BitmapFactory.decodeByteArray(out, 0, out.size)
    _uiState.update { it.copy(videoFrame = bitmap) }

    // Forward to WebRTC (every frame)
    webrtcViewModel?.pushVideoFrame(bitmap)
  }

  // Convert I420 (YYYYYYYY:UUVV) to NV21 (YYYYYYYY:VUVU)
  private fun convertI420toNV21(input: ByteArray, width: Int, height: Int): ByteArray {
    val output = ByteArray(input.size)
    val size = width * height
    val quarter = size / 4

    input.copyInto(output, 0, 0, size) // Y is the same

    for (n in 0 until quarter) {
      output[size + n * 2] = input[size + quarter + n] // V first
      output[size + n * 2 + 1] = input[size + n] // U second
    }
    return output
  }

  private fun handlePhotoData(photo: PhotoData) {
    val capturedPhoto =
        when (photo) {
          is PhotoData.Bitmap -> photo.bitmap
          is PhotoData.HEIC -> {
            val byteArray = ByteArray(photo.data.remaining())
            photo.data.get(byteArray)

            // Extract EXIF transformation matrix and apply to bitmap
            val exifInfo = getExifInfo(byteArray)
            val transform = getTransform(exifInfo)
            decodeHeic(byteArray, transform)
          }
        }
    _uiState.update { it.copy(capturedPhoto = capturedPhoto, isShareDialogVisible = true) }
  }

  // HEIC Decoding with EXIF transformation
  private fun decodeHeic(heicBytes: ByteArray, transform: Matrix): Bitmap {
    val bitmap = BitmapFactory.decodeByteArray(heicBytes, 0, heicBytes.size)
    return applyTransform(bitmap, transform)
  }

  private fun getExifInfo(heicBytes: ByteArray): ExifInterface? {
    return try {
      ByteArrayInputStream(heicBytes).use { inputStream -> ExifInterface(inputStream) }
    } catch (e: IOException) {
      Log.w(TAG, "Failed to read EXIF from HEIC", e)
      null
    }
  }

  private fun getTransform(exifInfo: ExifInterface?): Matrix {
    val matrix = Matrix()

    if (exifInfo == null) {
      return matrix // Identity matrix (no transformation)
    }

    when (
        exifInfo.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL,
        )
    ) {
      ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
        matrix.postScale(-1f, 1f)
      }
      ExifInterface.ORIENTATION_ROTATE_180 -> {
        matrix.postRotate(180f)
      }
      ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
        matrix.postScale(1f, -1f)
      }
      ExifInterface.ORIENTATION_TRANSPOSE -> {
        matrix.postRotate(90f)
        matrix.postScale(-1f, 1f)
      }
      ExifInterface.ORIENTATION_ROTATE_90 -> {
        matrix.postRotate(90f)
      }
      ExifInterface.ORIENTATION_TRANSVERSE -> {
        matrix.postRotate(270f)
        matrix.postScale(-1f, 1f)
      }
      ExifInterface.ORIENTATION_ROTATE_270 -> {
        matrix.postRotate(270f)
      }
      ExifInterface.ORIENTATION_NORMAL,
      ExifInterface.ORIENTATION_UNDEFINED -> {
        // No transformation needed
      }
    }

    return matrix
  }

  private fun applyTransform(bitmap: Bitmap, matrix: Matrix): Bitmap {
    if (matrix.isIdentity) {
      return bitmap
    }

    return try {
      val transformed = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
      if (transformed != bitmap) {
        bitmap.recycle()
      }
      transformed
    } catch (e: OutOfMemoryError) {
      Log.e(TAG, "Failed to apply transformation due to memory", e)
      bitmap
    }
  }

  override fun onCleared() {
    super.onCleared()
    stopStream()
    stateJob?.cancel()
  }

  class Factory(
      private val application: Application,
      private val wearablesViewModel: WearablesViewModel,
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(StreamViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST", "KotlinGenericsCast")
        return StreamViewModel(
            application = application,
            wearablesViewModel = wearablesViewModel,
        )
            as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
