package com.ciuciu.cameralibrary.config;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.concurrent.Executor;

public class CameraXManager {

    private ImageCapture mImageCapture;

    private AspectRatio mAspectRatio = AspectRatio.RATIO_16_9;
    private CameraX.LensFacing mLensFacing = CameraX.LensFacing.BACK;

    private Executor mainExecutor;

    private CameraXManagerListener mCameraManagerListener;

    private DisplayManager displayManager;

    private View attachedView;

    public CameraXManager(Context context) {
        mainExecutor = ContextCompat.getMainExecutor(context);
        displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(displayListener, null);
    }

    public void onAttach(View view) {
        attachedView = view;
    }

    public void setListener(CameraXManagerListener listener) {
        mCameraManagerListener = listener;
    }

    public Preview generatePreviewConfig(int rotation) {
        Size targetResolution = new Size(1920, 1080);
        //Log.d("CameraXManager", "rotation " + rotation);
        //Log.d("CameraXManager", "targetResolution " + targetResolution.getWidth() + " / " + targetResolution.getHeight());
        if (rotation == 0) {
            targetResolution = new Size(1080, 1920);
        }

        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setLensFacing(mLensFacing)
                .setTargetResolution(targetResolution)
                //.setTargetAspectRatio(mAspectRatio)
                .setTargetRotation(rotation)
                .build();

        return new Preview(previewConfig);
    }

    public ImageCapture generateCaptureConfig(int rotation) {
        Size targetResolution = new Size(1920, 1080);
        Log.d("CameraXManager", "rotation " + rotation);
        Log.d("CameraXManager", "targetResolution " + targetResolution.getWidth() + " / " + targetResolution.getHeight());

        if (rotation == 0) {
            targetResolution = new Size(1080, 1920);
        }

        ImageCaptureConfig captureConfig = new ImageCaptureConfig.Builder()
                .setLensFacing(mLensFacing)
                .setTargetResolution(targetResolution)
                //.setTargetAspectRatio(mAspectRatio)
//                .setTargetRotation(Surface.ROTATION_0)
                .setTargetRotation((rotation +3) % 3)
                .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .build();

        mImageCapture = new ImageCapture(captureConfig);
        return mImageCapture;
    }

    public void capturePhoto(File targetFile) {
        mImageCapture.takePicture(targetFile, createMetadata(), mainExecutor, new ImageCapture.OnImageSavedListener() {
            @Override
            public void onImageSaved(@NonNull File file) {
                if (mCameraManagerListener != null) {
                    mCameraManagerListener.onCapturePhotoSuccess(file);
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
        metadata.isReversedHorizontal = mLensFacing == CameraX.LensFacing.FRONT;
        return metadata;
    }

    private DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int i) {

        }

        @Override
        public void onDisplayRemoved(int i) {

        }

        @Override
        public void onDisplayChanged(int i) {

            /*if (mPreview != null && mTextureView.getDisplay() != null) {
                mPreview.setTargetRotation(mTextureView.getDisplay().getRotation());
                Log.d(TAG, "DisplayManager onDisplayChanged");
            }*/

            if (mImageCapture != null && attachedView != null && attachedView.getDisplay() != null) {
                mImageCapture.setTargetRotation(attachedView.getDisplay().getRotation());
            }
        }
    };
}
