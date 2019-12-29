package com.ciuciu.camerax.controller;

public interface CameraControllerListener {

    void switchLensFacing();

    void switchAspectRatio();

    void changeResolution();

    void changePreviewScale();

    void captureImage();

    void openImageGallery();
}
