# CameraX library for Android

----
## what is CameraX?

This library's base on Android CameraX library (Jetpack support library). It help you to make camera application with functions preview and capture or analyzer become easier, faster.

----


## Usage
----
graph TD
    CameraConfig[CameraConfig] -->|LensFacing, AspectRatio, TargetResolution, PreviewScaleType| CameraManager[CameraManager]
    ControllerView[ControllerView] -->|Capture, Preview Overlay... | CameraManager[CameraManager]
    Preview[Preview] -->|Preview| CameraManager
    ImageCapture[ImageCapture] -->|ImageCapture| CameraManager
    ImageAnalyzer[ImageAnalyzer] -->|ImageAnalyzer| CameraManager
    CameraManager --> UI[Fragment or Activity - UI]
   UI --> |Display on TextureView| CameraPreview[CameraPreview]

``![image text](https://github.com/ciuciuciu/CameraXLibrary/blob/master/diagram.png)``
