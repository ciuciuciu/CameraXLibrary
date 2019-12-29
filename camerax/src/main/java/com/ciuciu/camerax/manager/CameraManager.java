package com.ciuciu.camerax.manager;

import android.content.Context;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.core.content.ContextCompat;

import com.ciuciu.camerax.config.CameraConfig;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

public class CameraManager extends BaseCameraManager {

    private Executor mainExecutor;
    private CameraManagerListener mCameraManagerListener;

    public CameraManager(Context context) {
        mCameraConfig = new CameraConfig(context);
        mainExecutor = ContextCompat.getMainExecutor(context);
    }

    @Override
    public void setListener(CameraManagerListener listener) {
        mCameraManagerListener = listener;
    }

    public boolean switchLensFacing() {
        if (mCameraConfig != null) {
            return mCameraConfig.switchLensFacing();
        }
        return false;
    }

    public boolean changeAspectRatio() {
        if (mCameraConfig != null) {
            return mCameraConfig.switchAspectRatio();
        }
        return false;
    }

    public boolean changeCameraResolution(int rotation) {
        List<Size> supportedResolution = mCameraConfig.getSupportedResolution();
        Size targetResolution = mCameraConfig.getTargetResolution(rotation);
        if (rotation == 0) {
            targetResolution = new Size(targetResolution.getHeight(), targetResolution.getWidth());
        }
        int index = supportedResolution.indexOf(targetResolution);

        return mCameraConfig.setTargetResolution(supportedResolution.get((index + 1) % supportedResolution.size()));
    }

    public boolean changeCameraPreviewOutputScaleType() {
        return mCameraConfig.changePreviewScaleType();
    }

    public void capturePhoto(File targetFile) {
        mImageCapture.takePicture(targetFile, createMetadata(), mainExecutor, new ImageCapture.OnImageSavedListener() {
            @Override
            public void onImageSaved(@NonNull File photoFile) {
                if (mCameraManagerListener != null) {
                    mCameraManagerListener.onCapturePhotoSuccess(photoFile);
                }
            }

            @Override
            public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                cause.printStackTrace();
                if (mCameraManagerListener != null) {
                    mCameraManagerListener.onCapturePhotoError(imageCaptureError, message, cause);
                }
            }
        });
    }

    private ImageCapture.Metadata createMetadata() {
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        metadata.isReversedHorizontal = mCameraConfig.getLensFacing() == CameraX.LensFacing.FRONT;
        return metadata;
    }
}
