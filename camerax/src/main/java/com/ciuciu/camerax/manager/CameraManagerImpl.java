package com.ciuciu.camerax.manager;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;

import com.ciuciu.camerax.config.CameraConfig;
import com.ciuciu.camerax.controller.overlay.Frame;

import java.io.File;

public class CameraManagerImpl extends CameraManager {

    private DisplayManager mDisplayManager;
    private TextureView mAttachedTextureView;
    private int mAttachedTextureDisplayId = -1;

    public CameraManagerImpl(Context context) {
        super(context, new CameraConfig.Builder().build(context));
        initManager();
    }

    public CameraManagerImpl(Context context, @Nullable CameraConfig cameraConfig) {
        super(context, cameraConfig);
        initManager();
    }

    private void initManager() {
        mDisplayListener = new DisplayManager.DisplayListener() {
            @Override
            public void onDisplayAdded(int i) {

            }

            @Override
            public void onDisplayRemoved(int i) {

            }

            @Override
            public void onDisplayChanged(int displayId) {
                if (displayId == mAttachedTextureDisplayId) {
                    Log.d(TAG, "Rotation changed: " + displayId);
                    if (mAttachedTextureView != null && mAttachedTextureView.getDisplay() != null) {
                        cameraConfigShouldChange();
                    }
                    //imageAnalyzer?.setTargetRotation(view.display.rotation);
                }
            }
        };
    }

    @Override
    public void onAttach(@NonNull TextureView textureView) {
        mAttachedTextureView = textureView;
        mAttachedTextureDisplayId = textureView.getDisplay().getDisplayId();

        mDisplayManager = (DisplayManager) textureView.getContext().getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(mDisplayListener, null);

        if (mControllerView != null) {
            mControllerView.updateCameraConfig(mCameraConfig);
        }
    }

    @Override
    public void onDetach() {
        if (mDisplayManager != null) {
            mDisplayManager.unregisterDisplayListener(mDisplayListener);
        }
    }

    @Override
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
