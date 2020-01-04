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
