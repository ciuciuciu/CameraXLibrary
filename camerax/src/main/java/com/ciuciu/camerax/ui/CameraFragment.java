package com.ciuciu.camerax.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.fragment.app.Fragment;

import com.ciuciu.camerax.CameraHelper;
import com.ciuciu.camerax.R;
import com.ciuciu.camerax.camera.CameraManager;
import com.ciuciu.camerax.camera.CameraManagerListener;
import com.ciuciu.camerax.overlaycontroller.BaseControllerView;
import com.ciuciu.camerax.overlaycontroller.CameraControllerListener;
import com.ciuciu.camerax.overlaycontroller.CaptureControllerView;
import com.ciuciu.camerax.preview.CameraPreview;
import com.ciuciu.camerax.utils.FileUtils;

import java.io.File;

public class CameraFragment extends BaseCameraFragment implements CameraControllerListener, CameraManagerListener {

    private final String TAG = CameraFragment.class.getSimpleName();
    /**
     * Milliseconds used for UI animations
     */
    private final long ANIMATION_FAST_MILLIS = 50L;
    private final long ANIMATION_SLOW_MILLIS = 100L;

    private CameraPreview mCameraPreview;
    private CaptureControllerView mControllerView;

    private RelativeLayout resultContainer;
    private ImageView imageResult;
    private Button btnClose;

    private CameraManager mCameraManager;

    public static Fragment newInstance() {
        return new CameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_camera_x, container, false);

        resultContainer = rootView.findViewById(R.id.resultContainer);
        imageResult = rootView.findViewById(R.id.imageView);
        btnClose = rootView.findViewById(R.id.btnClose);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mCameraManager.onAttach(mCameraPreview.getTextureView().getDisplay());

    }

    @Override
    public void onStop() {
        super.onStop();
        mCameraManager.onDetach();
    }

    @Override
    protected void initFragment(@NonNull View view) {
        mCameraPreview = view.findViewById(R.id.cameraPreview);

        mCameraManager = new CameraManager(getContext());
        mCameraManager.setListener(CameraFragment.this);

        openCamera();
    }

    @Override
    public void openCamera() {
        if (CameraHelper.hasPermissions(this) == false) {
            return;
        }

        mCameraPreview.post(new Runnable() {
            @Override
            public void run() {
                CameraX.unbindAll();

                int rotation = mCameraPreview.getTextureView().getDisplay().getRotation();

                // generate preview config
                Preview preview = mCameraManager.generatePreviewConfig(rotation);

                mCameraPreview.setPreview(preview);
                mCameraPreview.setOverlayView(createOverlayView());

                // generate capture config
                ImageCapture imageCapture = mCameraManager.generateCaptureConfig(rotation);

                CameraX.bindToLifecycle(CameraFragment.this, preview, imageCapture);
            }
        });
    }

    @Override
    public BaseControllerView createOverlayView() {
        mControllerView = new CaptureControllerView(getContext());
        mControllerView.setControllerListener(this);
        return mControllerView;
    }

    @Override
    public void switchLensFacing() {
        mCameraManager.getCameraConfig().switchLensFacing();
        openCamera();
    }

    @Override
    public void switchAspectRatio() {

    }

    @Override
    public void changeResolution() {

    }

    @Override
    public void changePreviewScale() {

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

    }

    @Override
    public void onCapturePhotoSuccess(@NonNull File photoFile) {
        // Update the gallery thumbnail with latest picture taken
        if (mControllerView != null) {
            //mControllerView.setGalleryThumbnail(photoFile);
        }

        resultContainer.setVisibility(View.VISIBLE);
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
        imageResult.setImageBitmap(bitmap);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCapturePhotoError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {

    }


}
