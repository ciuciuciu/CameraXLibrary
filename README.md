# CameraX library for Android

----
## what is CameraX?

This library's base on Android CameraX library (Jetpack support library). It help you to make camera application with functions preview and capture or analyzer become easier, faster.

----


## Usage
----
### Sequence diagram

```mermaid
graph TD
    CameraConfig[CameraConfig] -->|Text| CameraManager[CameraManager]
    ControllerView[ControllerView] -->|Text| CameraManager[CameraManager]
    C[ImageCapture] -->|Text| CameraManager
    D[ImageAnalyzer] -->|Text| CameraManager
    CameraManager -->|Text| UI[Fragment or Activity - UI]
   UI --> |Text| CameraPreview[CameraPreview]
```
----
