/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.github.rubensousa.macgyver

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.meta.wearable.dat.camera.types.StreamState
import com.meta.wearable.dat.mockdevice.MockDeviceKit
import com.meta.wearable.dat.mockdevice.api.GlassesModel
import io.github.rubensousa.macgyver.stream.StreamViewModel
import java.io.File
import java.io.FileOutputStream
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement

/**
 * DAT 0.9 integration checks. MockDeviceKit and runtime permissions must be ready before
 * MainActivity is created: both Wearables and the activity initialize lazily, and JUnit's
 * @Before runs too late for that precondition.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class InstrumentationTest {

  companion object {
    private const val PREFS_NAME = "macgyver_settings"
    private const val CAPTURE_SOURCE_KEY = "captureSource"
    private const val GLASSES_CAPTURE_SOURCE = "glasses"
    private const val UI_TIMEOUT_MS = 15_000L
    private const val CAMERA_TIMEOUT_MS = 30_000L
  }

  private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
  private val mockDeviceKit = MockDeviceKit.getInstance(targetContext)
  private val composeTestRule = createAndroidComposeRule<MainActivity>()

  private val mockAndSettingsRule =
      TestRule { base: Statement, _: Description ->
        object : Statement() {
          override fun evaluate() {
            // MainActivity reads this preference during onCreate. Set it and enable the mock
            // before the compose rule creates the activity, not in @Before.
            targetContext
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(CAPTURE_SOURCE_KEY, GLASSES_CAPTURE_SOURCE)
                .commit()
            mockDeviceKit.enable()
            try {
              base.evaluate()
            } finally {
              mockDeviceKit.disable()
              targetContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
            }
          }
        }
      }

  private val permissionsRule =
      GrantPermissionRule.grant(
          Manifest.permission.BLUETOOTH_CONNECT,
          Manifest.permission.CAMERA,
          Manifest.permission.RECORD_AUDIO,
      )

  @get:Rule
  val rules: TestRule =
      // Grant BLUETOOTH_CONNECT before MockDeviceKit initializes DAT. RuleChain evaluates
      // outer rules first, so reversing these two rules is a functional requirement.
      RuleChain.outerRule(permissionsRule).around(mockAndSettingsRule).around(composeTestRule)

  @Test
  fun showsGlassesHomeWhenNoMockDeviceIsPaired() {
    val homeTip = targetContext.getString(R.string.home_tip_video)

    composeTestRule.waitUntilExactlyOneExists(hasText(homeTip), timeoutMillis = UI_TIMEOUT_MS)
  }

  @Test
  fun mockDeviceDeliversFirstFrameAndCapturesPhoto() {
    val streamViewModel = startMockCameraStream()

    // The first actual frame, rather than a started-stream state, is the readiness boundary.
    composeTestRule.waitUntil(CAMERA_TIMEOUT_MS) { streamViewModel.uiState.value.videoFrame != null }

    streamViewModel.capturePhoto()
    composeTestRule.waitUntil(CAMERA_TIMEOUT_MS) { streamViewModel.uiState.value.capturedPhoto != null }

    streamViewModel.stopStream()
    composeTestRule.waitUntil(UI_TIMEOUT_MS) {
      streamViewModel.uiState.value.streamSessionState == StreamState.STOPPED
    }
  }

  @Test
  fun mockCameraTeardownIsIdempotent() {
    val streamViewModel = startMockCameraStream()

    composeTestRule.waitUntil(CAMERA_TIMEOUT_MS) { streamViewModel.uiState.value.videoFrame != null }
    streamViewModel.stopStream()
    streamViewModel.stopStream()

    composeTestRule.waitUntil(UI_TIMEOUT_MS) {
      streamViewModel.uiState.value.streamSessionState == StreamState.STOPPED
    }
    assertEquals(StreamState.STOPPED, streamViewModel.uiState.value.streamSessionState)
  }

  private fun startMockCameraStream(): StreamViewModel {
    val device = mockDeviceKit.pairGlasses(GlassesModel.RAYBAN_META).getOrThrow()
    device.powerOn()
    device.don()
    device.services.camera.setCameraFeed(getFileUri("plant.mp4"))
    device.services.camera.setCapturedImage(getFileUri("plant.png"))

    val wearablesViewModel = composeTestRule.activity.viewModel
    wearablesViewModel.startMonitoring()
    composeTestRule.waitUntil(UI_TIMEOUT_MS) { wearablesViewModel.uiState.value.hasActiveDevice }

    return StreamViewModel(
            application = composeTestRule.activity.application,
            wearablesViewModel = wearablesViewModel,
        )
        .also { it.startStream() }
  }

  private fun getFileUri(assetName: String): Uri {
    val cacheFile = File(targetContext.cacheDir, assetName)
    InstrumentationRegistry.getInstrumentation().context.assets.open(assetName).use { input ->
      FileOutputStream(cacheFile).use { output -> input.copyTo(output) }
    }
    return Uri.fromFile(cacheFile)
  }
}
