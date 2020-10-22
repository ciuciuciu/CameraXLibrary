package com.ciuciu.camerax.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;

import com.ciuciu.camerax.config.CameraConfig;
import com.ciuciu.camerax.controller.overlay.BaseOverlayView;
import com.ciuciu.camerax.controller.overlay.Frame;
import com.ciuciu.camerax.utils.ImageUtil;

import java.io.File;
import java.io.FileOutputStream;

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

                        if (mImageAnalysis != null) {
                            mImageAnalysis.setTargetRotation(mAttachedTextureView.getDisplay().getRotation());
                        }
                    }
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

        mImageCapture.takePicture(tempFile, createMetadata(), mainExecutor, new ImageCapture.OnImageSavedListener() {
            @Override
            public void onImageSaved(@NonNull File photoFile) {
                Frame frameOutputImage = null;

                BaseOverlayView overlayView = mControllerView.getOverlayView();
                if (overlayView != null) {
                    frameOutputImage = overlayView.getOutputTransformFrame(mCameraConfig.getTargetResolution(overlayView.getDisplay().getRotation()));
                }

                if (frameOutputImage == null) {
                    // rename tempFile to targetFile
                    photoFile.renameTo(targetFile);
                    if (mCameraManagerListener != null) {
                        mCameraManagerListener.onCapturePhotoSuccess(targetFile);
                    }
                } else {
                    // crop image and save to targetFile
                    Frame finalFrameOutputImage = frameOutputImage;
                    mainExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            FileOutputStream outputStream = null;
                            try {
                                outputStream = new FileOutputStream(targetFile);
                                Bitmap bitmap = ImageUtil.rotateImage(BitmapFactory.decodeFile(photoFile.getPath()), photoFile.getPath());
                                Bitmap croppedBmp = Bitmap.createBitmap(bitmap,
                                        (int) finalFrameOutputImage.getLeft(),
                                        (int) finalFrameOutputImage.getTop(),
                                        (int) finalFrameOutputImage.getWidth(),
                                        (int) finalFrameOutputImage.getHeight());

                                croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
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
