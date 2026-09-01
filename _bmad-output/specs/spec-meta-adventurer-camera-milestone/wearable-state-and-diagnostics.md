# Wearable State and Diagnostics Contract

## Hardware states

| State | Meaning |
|---|---|
| `NO_DEVICE` | No eligible wearable is connected. |
| `DEVICE_CONNECTED` | A wearable is connected and identified. |
| `SESSION_STARTING` | DAT device-session creation is in progress. |
| `SESSION_READY` | The device session is usable. |
| `CAMERA_STARTING` | Camera creation/start is in progress. |
| `STREAM_WAITING` | The stream reports startup but no real frame has arrived. |
| `FIRST_FRAME_RECEIVED` | The first real frame establishes vision readiness. |
| `STREAM_ACTIVE` | Frames are arriving and telemetry is updating. |
| `STREAM_PAUSED` | Streaming is paused by lifecycle or device state. |
| `STREAM_FAILED` | Camera/stream operation failed with a classified error. |
| `DISCONNECTED` | A formerly connected device has disconnected and cleanup/recovery applies. |

`STREAM_WAITING` must never be treated as vision-ready. A transition to readiness requires an actual frame callback carrying usable image data.

## Device handling and logging

Handle `DeviceType.META_GLASSES` explicitly and do not reject it for not being `RAYBAN_META`. Record:

- device identifier, name, type, and capabilities;
- battery and thermal state when exposed by DAT;
- DAT registration and device-session state;
- camera lifecycle and stream state;
- first-frame and last-frame timestamps;
- disconnect reason and classified error.

Structured session records include `session_id`, `device_type`, DAT state, camera state, first-frame latency, frames received, frames forwarded, disconnect reason, and errors by layer. Never log API/OAuth secrets, access tokens, or raw private audio by default.

Errors use the narrowest applicable layer: `DEVICE`, `DAT`, `CAMERA`, `NETWORK`, or `AUTH`. Do not surface a generic “AI failed” message for a device or camera failure.

## Developer diagnostics screen

Show at minimum:

- Active device, Device type, Device ID;
- DAT registration state, Device session state, Camera state, Stream state;
- First frame received, Last frame timestamp, Incoming FPS, Total received frames, Frames sent to AI;
- Bluetooth input route and Bluetooth output route;
- Battery level and Thermal state;
- Realtime provider and Realtime connection state;
- Hermes gateway state;
- Last tool call and Last error.

Realtime, Hermes, tool-call, and not-yet-wired audio fields must display a truthful value such as `NOT_CONFIGURED` or `NOT_AVAILABLE`; their presence does not authorize those integrations in this milestone.

The DAT registration field is a direct projection of `Wearables.registrationState`; registration failures are surfaced from `Wearables.registrationErrorStream`. The camera session must not start until registration reaches `REGISTERED`.
