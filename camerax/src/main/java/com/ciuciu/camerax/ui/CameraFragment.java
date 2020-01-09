package com.ciuciu.camerax.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.fragment.app.Fragment;

import com.ciuciu.camerax.CameraHelper;
import com.ciuciu.camerax.R;
import com.ciuciu.camerax.controller.CameraControllerListener;
import com.ciuciu.camerax.controller.CaptureControllerView;
import com.ciuciu.camerax.manager.CameraManager;
import com.ciuciu.camerax.manager.CameraManagerImpl;
import com.ciuciu.camerax.manager.CameraManagerListener;
import com.ciuciu.camerax.preview.CameraPreview;
import com.ciuciu.camerax.ui.viewer.PhotoViewerActivity;
import com.ciuciu.camerax.utils.FileUtils;

import java.io.File;

public class CameraFragment extends BaseCameraFragment {

    private CameraPreview mCameraPreview;
    private CameraManager mCameraManager;
    private File mLastCaptureFile;

    public static Fragment newInstance() {
        return new CameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_camera_x, container, false);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCameraManager.onDetach();
    }

    @Override
    protected void initFragment(@NonNull View view) {
        mCameraPreview = view.findViewById(R.id.cameraPreview);

        mCameraManager = new CameraManagerImpl(getContext());

        CaptureControllerView mControllerView = new CaptureControllerView(getContext());
        mControllerView.setControllerListener(cameraControllerListener);
        mCameraManager.setControllerView(mControllerView);
        mCameraManager.setListener(cameraManagerListener);

        openCamera();
    }

    @Override
    public void openCamera() {
        if (CameraHelper.hasPermissions(this) == false) {
            return;
        }

        TextureView textureView = mCameraPreview.getTextureView();
        textureView.post(new Runnable() {
            @Override
            public void run() {
                CameraX.unbindAll();

                // generate preview config
                Preview preview = mCameraManager.generatePreviewConfig(textureView.getDisplay().getRotation());
                mCameraPreview.setPreview(preview, mCameraManager.getCameraConfig().getPreviewScaleType());
                mCameraPreview.setOverlayView(mCameraManager.getControllerView());

                // generate capture config
                ImageCapture imageCapture = mCameraManager.generateCaptureConfig(textureView.getDisplay().getRotation());

                // Attach to manager and bind to lifecycle
                mCameraManager.onAttach(mCameraPreview.getTextureView());
                CameraX.bindToLifecycle(CameraFragment.this, preview, imageCapture);
            }
        });
    }


    CameraControllerListener cameraControllerListener = new CameraControllerListener() {

        @Override
        public void switchLensFacing() {
            mCameraManager.switchLensFacing();
        }

        @Override
        public void switchAspectRatio() {
            mCameraManager.changeAspectRatio();
        }

        @Override
        public void changeResolution() {
            mCameraManager.changeCameraResolution(mCameraPreview.getTextureView().getDisplay().getRotation());
        }

        @Override
        public void changePreviewScale() {
            mCameraManager.changeCameraPreviewOutputScaleType();
        }

        @Override
        public void captureImage() {
            mCameraManager.capturePhoto(
                    FileUtils.createFile(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            FileUtils.FILENAME,
                            FileUtils.PHOTO_EXTENSION));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Display flash animation to indicate that photo was captured
                getView().postDelayed(() -> {
                    getView().setForeground(new ColorDrawable(Color.WHITE));
                    getView().postDelayed(() -> getView().setForeground(null), ANIMATION_FAST_MILLIS);
                }, ANIMATION_SLOW_MILLIS);
            }
        }

        @Override
        public void openImageGallery() {
            if (mLastCaptureFile != null) {
                PhotoViewerActivity.startActivity(getContext(), mLastCaptureFile);
            }
        }
    };

    CameraManagerListener cameraManagerListener = new CameraManagerListener() {

        @Override
        public void onCameraConfigChanged() {
            openCamera();
        }

        @Override
        public void onCapturePhotoSuccess(@NonNull File photoFile) {
            mLastCaptureFile = photoFile;

            // Update the gallery thumbnail with latest picture taken
            if (mCameraManager.getControllerView() != null && mCameraManager.getControllerView() instanceof CaptureControllerView) {
                ((CaptureControllerView) mCameraManager.getControllerView()).setGalleryThumbnail(photoFile);
            }
        }

        @Override
        public void onCapturePhotoError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {

        }
    };


}
