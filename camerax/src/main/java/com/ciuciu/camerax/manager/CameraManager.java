package com.ciuciu.camerax.manager;

import android.content.Context;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.core.content.ContextCompat;

import com.ciuciu.camerax.config.CameraConfig;
import com.ciuciu.camerax.controller.overlay.Frame;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

public class CameraManager extends BaseCameraManager {

    private Executor mainExecutor;

    public CameraManager(Context context) {
        mCameraConfig = new CameraConfig.Builder().build(context);
        mainExecutor = ContextCompat.getMainExecutor(context);
    }

    public CameraManager(Context context, @Nullable CameraConfig cameraConfig) {
        mCameraConfig = cameraConfig;
        mainExecutor = ContextCompat.getMainExecutor(context);
    }

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

    public void capturePhoto(File targetFile) {

        File tempFile = new File(targetFile.getParent(), "temp.dat");
        if (tempFile.exists()) {
            tempFile.delete();
        }

        Frame cropFrame = null;
        if (mControllerView != null && mControllerView.getOverlayView() != null) {
            if (mControllerView.getOverlayView().getInnerFrame() != null) {
                //cropFrame= mControllerView.getOverlayView().getOutputTransformFrame(image, rotationDegrees);
            }
        }

        mImageCapture.takePicture(tempFile, createMetadata(), mainExecutor, new ImageCapture.OnImageSavedListener() {
            @Override
            public void onImageSaved(@NonNull File photoFile) {
                if (cropFrame == null) {
                    // rename tempFile to targetFile
                    photoFile.renameTo(targetFile);
                    if (mCameraManagerListener != null) {
                        mCameraManagerListener.onCapturePhotoSuccess(targetFile);
                    }
                } else {
                    // crop image and save to targetFile
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

        /*mImageCapture.takePicture(mainExecutor, new ImageCapture.OnImageCapturedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCaptureSuccess(ImageProxy image, int rotationDegrees) {

                if (mControllerView != null && mControllerView.getOverlayView() != null) {
                    if (mControllerView.getOverlayView().getInnerFrame() != null) {
                        Frame frame = mControllerView.getOverlayView().getOutputTransformFrame(image, rotationDegrees);
                        if (frame != null) {
                            image.setCropRect(frame.toRect());
                        }
                    }
                }

                ImageCapture.Metadata metadata = createMetadata();
//                CameraXExecutors
//                        .ioExecutor()
//                        .execute(
//                                new ImageSaver(
//                                        image,
//                                        targetFile,
//                                        rotationDegrees,
//                                        metadata.isReversedHorizontal,
//                                        metadata.isReversedVertical,
//                                        null,
//                                        mainExecutor,
//                                        new ImageSaver.OnImageSavedListener() {
//                                            @Override
//                                            public void onImageSaved(File file) {
//                                                if (mCameraManagerListener != null) {
//                                                    mCameraManagerListener.onCapturePhotoSuccess(file);
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onError(ImageSaver.SaveError saveError, String message, @Nullable Throwable cause) {
//                                                cause.printStackTrace();
//                                                if (mCameraManagerListener != null) {
//                                                    //mCameraManagerListener.onCapturePhotoError(saveError, message, cause);
//                                                }
//                                            }
//                                        }));
            }

            @Override
            public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                super.onError(imageCaptureError, message, cause);
                cause.printStackTrace();
                if (mCameraManagerListener != null) {
                    mCameraManagerListener.onCapturePhotoError(imageCaptureError, message, cause);
                }
            }
        });*/
    }

    private ImageCapture.Metadata createMetadata() {
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        metadata.isReversedHorizontal = mCameraConfig.getLensFacing() == CameraX.LensFacing.FRONT;
        return metadata;
    }
}
