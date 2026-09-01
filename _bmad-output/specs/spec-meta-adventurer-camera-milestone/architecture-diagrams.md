# Architecture Diagrams

## Milestone boundary

```mermaid
flowchart LR
    A[Meta Adventurer\ncamera] --> B[VisionClaw Android]
    B --> C[WearableAdapter]
    C --> D[Meta Wearables DAT 0.9.x]
    B --> E[Preview and diagnostics]
    F[RealtimeProvider]:::future
    G[Operator / Hermes]:::future
    H[Tools]:::future
    C -. independent boundary .-> F
    F -. future .-> G
    G -. future .-> H
    classDef future fill:#eee,stroke:#999,stroke-dasharray: 5 5
```

Only the solid-path hardware adapter, preview, and diagnostics are implemented in this milestone. Dashed nodes define architectural boundaries, not current scope.

## Readiness and recovery

```mermaid
stateDiagram-v2
    [*] --> NO_DEVICE
    NO_DEVICE --> DEVICE_CONNECTED
    DEVICE_CONNECTED --> SESSION_STARTING
    SESSION_STARTING --> SESSION_READY
    SESSION_READY --> CAMERA_STARTING
    CAMERA_STARTING --> STREAM_WAITING
    STREAM_WAITING --> FIRST_FRAME_RECEIVED: usable frame callback
    FIRST_FRAME_RECEIVED --> STREAM_ACTIVE
    STREAM_ACTIVE --> STREAM_PAUSED
    STREAM_PAUSED --> STREAM_ACTIVE: frames resume
    STREAM_WAITING --> STREAM_FAILED
    STREAM_ACTIVE --> STREAM_FAILED
    DEVICE_CONNECTED --> STREAM_FAILED: session creation fails
    SESSION_STARTING --> STREAM_FAILED: session start fails
    SESSION_READY --> STREAM_FAILED: capability precondition fails
    CAMERA_STARTING --> STREAM_FAILED: camera or stream start fails
    FIRST_FRAME_RECEIVED --> STREAM_FAILED
    STREAM_PAUSED --> STREAM_FAILED
    STREAM_FAILED --> SESSION_STARTING: recreate failed session
    STREAM_FAILED --> CAMERA_STARTING: retry camera on usable session
    SESSION_STARTING --> DEVICE_CONNECTED: user stop
    SESSION_READY --> DEVICE_CONNECTED: user stop
    CAMERA_STARTING --> DEVICE_CONNECTED: user stop
    STREAM_WAITING --> DEVICE_CONNECTED: user stop
    FIRST_FRAME_RECEIVED --> DEVICE_CONNECTED: user stop
    STREAM_ACTIVE --> DEVICE_CONNECTED: user stop
    STREAM_PAUSED --> DEVICE_CONNECTED: user stop
    STREAM_FAILED --> DEVICE_CONNECTED: abort retry
    DEVICE_CONNECTED --> DISCONNECTED
    SESSION_STARTING --> DISCONNECTED
    SESSION_READY --> DISCONNECTED
    CAMERA_STARTING --> DISCONNECTED
    STREAM_WAITING --> DISCONNECTED
    FIRST_FRAME_RECEIVED --> DISCONNECTED
    STREAM_ACTIVE --> DISCONNECTED
    STREAM_PAUSED --> DISCONNECTED
    STREAM_FAILED --> DISCONNECTED
    DISCONNECTED --> NO_DEVICE: cleanup complete
```
