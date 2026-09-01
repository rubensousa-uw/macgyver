# Camera Acceptance Contract

## Preview and measurements

Provide a live camera preview and record:

- stream startup latency;
- first-frame latency;
- camera reconnect latency;
- effective incoming FPS;
- total received frames;
- frames forwarded to an AI layer, expected to remain zero until that layer exists.

## Physical setup

The test target is Meta Adventurer Standard 49 mm paired with a Samsung Galaxy Z Fold7 running Android 16 and Meta AI 286.1.0.17.162. Before testing, record any version changes, whether Developer Mode is enabled for the linked Adventurer, the `Wearables.registrationState` result, selected DAT version, app commit, build variant, and network conditions. Registration must reach `REGISTERED` before the camera-session gates run.

## Manual test sequence

For every case, record state transitions, first-frame result, latency, recovery result, and any layer-specific error:

1. Connect the glasses and confirm explicit `META_GLASSES` identification.
2. Start the camera and wait for a real preview frame.
3. Disconnect and reconnect the glasses.
4. Turn the glasses off and on.
5. Background and foreground the app.
6. Lock and unlock the phone.
7. Change from Wi-Fi to mobile data.
8. Repeat with Meta AI open and closed.
9. Start and stop the camera repeatedly.

## Acceptance gates

All are required for physical acceptance:

- Adventurer is detected as `DeviceType.META_GLASSES`.
- The camera starts through the DAT 0.9.x lifecycle.
- Real frames arrive and the live preview updates.
- `FIRST_FRAME_RECEIVED` occurs before the pipeline reports readiness.
- Diagnostics and telemetry remain internally consistent.
- The session cleans up and recovers after disconnect/reconnect.

Compilation, emulator tests, mocks, and phone-camera fallback cannot satisfy these hardware gates. If the glasses are unavailable or any gate fails, report the exact result and keep `adventurer-camera-working` uncreated.
