package com.ciuciu.camerax.manager;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.Size;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;

import com.ciuciu.camerax.config.CameraConfig;
import com.ciuciu.camerax.controller.BaseControllerView;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class CameraManager {

    protected final String TAG = "CameraManager";

    protected Preview mPreview;
    protected ImageCapture mImageCapture;

    protected CameraConfig mCameraConfig;
    protected BaseControllerView mControllerView;

    protected Executor mainExecutor;
    protected DisplayManager.DisplayListener mDisplayListener;

    protected CameraManagerListener mCameraManagerListener;

    public CameraManager(Context context, @Nullable CameraConfig cameraConfig) {
        mCameraConfig = cameraConfig;
        mainExecutor = ContextCompat.getMainExecutor(context);
    }

    public abstract void onAttach(@NonNull TextureView textureView);

    public abstract void onDetach();

    public abstract void capturePhoto(File targetFile);

    public void setListener(CameraManagerListener listener) {
        mCameraManagerListener = listener;
    }

    public CameraConfig getCameraConfig() {
        return mCameraConfig;
    }

    public void setControllerView(BaseControllerView controllerView) {
        mControllerView = controllerView;
    }

    public BaseControllerView getControllerView() {
        return mControllerView;
    }

    public Preview generatePreviewConfig(int rotation) {
        Size targetResolution = mCameraConfig.getTargetResolution(rotation);

        PreviewConfig previewConfig;
        if (targetResolution == null) {
            previewConfig = new PreviewConfig.Builder()
                    .setLensFacing(mCameraConfig.getLensFacing())
                    .setTargetAspectRatio(mCameraConfig.getAspectRatio())
                    .setTargetRotation(rotation)
                    .build();

        } else {
            previewConfig = new PreviewConfig.Builder()
                    .setLensFacing(mCameraConfig.getLensFacing())
                    .setTargetResolution(targetResolution)
                    .setTargetRotation(rotation)
                    .build();
        }

        mPreview = new Preview(previewConfig);
        return mPreview;
    }

    public ImageCapture generateCaptureConfig(int rotation) {
        Size targetResolution = mCameraConfig.getTargetResolution(rotation);

        ImageCaptureConfig captureConfig;
        if (targetResolution == null) {
            captureConfig = new ImageCaptureConfig.Builder()
                    .setLensFacing(mCameraConfig.getLensFacing())
                    .setTargetAspectRatio(mCameraConfig.getAspectRatio())
                    .setTargetRotation(rotation)
                    .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                    .build();
        } else {
            captureConfig = new ImageCaptureConfig.Builder()
                    .setLensFacing(mCameraConfig.getLensFacing())
                    .setTargetResolution(targetResolution)
                    .setTargetRotation(rotation)
                    .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                    .build();
        }

        mImageCapture = new ImageCapture(captureConfig);
        return mImageCapture;
    }


    /**
     * Camera Config change
     */
    public void switchLensFacing() {
        if (mCameraConfig.switchLensFacing()) {
            cameraConfigShouldChange();
        }
    }

    public void changeAspectRatio() {
        if (mCameraConfig.switchAspectRatio()) {
            cameraConfigShouldChange();
        }
    }

    public void changeCameraResolution(int rotation) {
        List<Size> supportedResolution = mCameraConfig.getSupportedResolution();
        Size targetResolution = mCameraConfig.getTargetResolution(rotation);
        if (rotation == 0) {
            targetResolution = new Size(targetResolution.getHeight(), targetResolution.getWidth());
        }
        int index = supportedResolution.indexOf(targetResolution);

        if (mCameraConfig.setTargetResolution(supportedResolution.get((index + 1) % supportedResolution.size()))) {
            cameraConfigShouldChange();
        }
    }

    public void changeCameraPreviewOutputScaleType() {
        if (mCameraConfig.changePreviewScaleType()) {
            cameraConfigShouldChange();
        }
    }

    protected void cameraConfigShouldChange() {
        if (mCameraManagerListener != null) {
            mCameraManagerListener.onCameraConfigChanged();
        }
    }
    /**
     * End Camera Config change
     */
}
