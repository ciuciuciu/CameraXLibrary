package com.ciuciu.camerax.camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageCapture;

import java.io.File;

public interface CameraManagerListener {

    void onCapturePhotoSuccess(@NonNull File photoFile);

    void onCapturePhotoError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause);
}
