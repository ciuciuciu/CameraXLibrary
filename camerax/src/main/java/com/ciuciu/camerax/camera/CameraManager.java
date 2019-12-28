package com.ciuciu.camerax.camera;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;

import com.ciuciu.camerax.camera.config.CameraConfig;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

public class CameraManager implements CameraManagerView {

    private final String TAG = CameraManager.class.getSimpleName();

    private Context mContext;
    private DisplayManager mDisplayManager;
    private Executor mainExecutor;
    private CameraManagerListener mCameraManagerListener;

    private Preview mPreview;
    private ImageCapture mImageCapture;

    private CameraConfig mCameraConfig;

    private TextureView mAttachedTextureView;
    private int mTextureDisplayId = -1;

    private DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int i) {

        }

        @Override
        public void onDisplayRemoved(int i) {

        }

        @Override
        public void onDisplayChanged(int displayId) {
            if (displayId == CameraManager.this.mTextureDisplayId) {
                Log.d(TAG, "Rotation changed: " + displayId);
                if (mPreview != null && mAttachedTextureView != null && mAttachedTextureView.getDisplay() != null) {
                    Log.d(TAG, "Set new rotation " + mAttachedTextureView.getDisplay().getRotation() + " for Preview");
                    mPreview.setTargetRotation(mAttachedTextureView.getDisplay().getRotation());
                }

                if (mImageCapture != null && mAttachedTextureView != null && mAttachedTextureView.getDisplay() != null) {
                    Log.d(TAG, "Set new rotation " + mAttachedTextureView.getDisplay().getRotation() + " for ImageCapture");
                    mImageCapture.setTargetRotation(mAttachedTextureView.getDisplay().getRotation());
                }
                //imageAnalyzer?.setTargetRotation(view.display.rotation);
            }
        }
    };

    public CameraManager(Context context) {
        mContext = context;
        mCameraConfig = new CameraConfig(mContext);
        mainExecutor = ContextCompat.getMainExecutor(context);
    }

    @Override
    public void setListener(CameraManagerListener listener) {
        mCameraManagerListener = listener;
    }

    @Override
    public void onAttach(@NonNull TextureView textureView) {
        mTextureDisplayId = textureView.getDisplay().getDisplayId();
        mAttachedTextureView = textureView;
        mDisplayManager = (DisplayManager) textureView.getContext().getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(displayListener, null);
    }

    @Override
    public void onDetach() {
        mDisplayManager.unregisterDisplayListener(displayListener);
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

    public CameraConfig getCameraConfig() {
        return mCameraConfig;
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

    public boolean changeCameraPreviewOutputScaleType(){
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
