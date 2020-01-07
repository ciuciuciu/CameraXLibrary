package com.ciuciu.camerax.manager;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;

import com.ciuciu.camerax.config.CameraConfig;
import com.ciuciu.camerax.controller.BaseControllerView;

public abstract class BaseCameraManager {

    protected final String TAG = "CameraManager";

    protected CameraConfig mCameraConfig;
    protected BaseControllerView mControllerView;

    protected Preview mPreview;
    protected ImageCapture mImageCapture;

    private DisplayManager mDisplayManager;
    private TextureView mAttachedTextureView;
    private int mAttachedTextureDisplayId = -1;

    private DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int i) {

        }

        @Override
        public void onDisplayRemoved(int i) {

        }

        @Override
        public void onDisplayChanged(int displayId) {
            if (displayId == BaseCameraManager.this.mAttachedTextureDisplayId) {
                Log.d(TAG, "Rotation changed: " + displayId);
                if (mPreview != null && mAttachedTextureView != null && mAttachedTextureView.getDisplay() != null) {
                    Log.d(TAG, "Set new rotation " + mAttachedTextureView.getDisplay().getRotation() + " for Preview");
                    mPreview.setTargetRotation(mAttachedTextureView.getDisplay().getRotation());
                }

                if (mImageCapture != null && mAttachedTextureView != null && mAttachedTextureView.getDisplay() != null) {
                    Log.d(TAG, "Set new rotation " + mAttachedTextureView.getDisplay().getRotation() + " for ImageCapture");
                    //mImageCapture.setTargetRotation(mAttachedTextureView.getDisplay().getRotation());
                    generateCaptureConfig(mAttachedTextureView.getDisplay().getRotation());
                }
                //imageAnalyzer?.setTargetRotation(view.display.rotation);
            }
        }
    };

    public void onAttach(@NonNull TextureView textureView) {
        mAttachedTextureView = textureView;
        mAttachedTextureDisplayId = textureView.getDisplay().getDisplayId();

        mDisplayManager = (DisplayManager) textureView.getContext().getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(displayListener, null);

        if (mControllerView != null) {
            mControllerView.updateCameraConfig(mCameraConfig);
        }
    }

    public void onDetach() {
        if (mDisplayManager != null) {
            mDisplayManager.unregisterDisplayListener(displayListener);
        }
    }

    public abstract void setListener(CameraManagerListener listener);

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

}
