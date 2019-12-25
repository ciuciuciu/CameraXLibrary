package com.ciuciu.cameralibrary.config;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageCapture;

import java.io.File;

public interface CameraXManagerListener {

    void onCapturePhotoSuccess(@NonNull File file);

    void onCapturePhotoError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause);
}
