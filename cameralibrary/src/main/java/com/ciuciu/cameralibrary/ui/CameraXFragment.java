package com.ciuciu.cameralibrary.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.fragment.app.Fragment;

import com.ciuciu.cameralibrary.CameraHelper;
import com.ciuciu.cameralibrary.R;
import com.ciuciu.cameralibrary.config.CameraXManager;
import com.ciuciu.cameralibrary.config.CameraXManagerListener;
import com.ciuciu.cameralibrary.overlaycontroller.BaseOverlayView;
import com.ciuciu.cameralibrary.overlaycontroller.CameraControllerListener;
import com.ciuciu.cameralibrary.overlaycontroller.CaptureControllerView;
import com.ciuciu.cameralibrary.preview.CameraPreview;
import com.ciuciu.cameralibrary.utils.FileUtils;

import java.io.File;

public class CameraXFragment extends BaseCameraFragment implements CameraControllerListener, CameraXManagerListener {

    private CameraPreview mCameraPreview;
    private RelativeLayout resultContainer;
    private ImageView imageResult;
    private Button btnClose;


    private CameraXManager mCameraManager;

    public static Fragment newInstance() {
        return new CameraXFragment();
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
    protected void initFragment(@NonNull View view) {
        mCameraPreview = view.findViewById(R.id.cameraPreview);
        mCameraManager = new CameraXManager(getContext());
        mCameraManager.onAttach(getView());
        mCameraManager.setListener(this);
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


                int rotation = (int) mCameraPreview.getRotation();
                // generate preview config
                Preview preview = mCameraManager.generatePreviewConfig(rotation);

                mCameraPreview.setPreview(preview);
                BaseOverlayView overlayView = createOverlayView();
                if (overlayView != null) {
                    mCameraPreview.setOverlayView(overlayView);
                }

                // generate capture config
                WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

                ImageCapture imageCapture = mCameraManager.generateCaptureConfig(windowManager.getDefaultDisplay().getRotation());

                CameraX.bindToLifecycle(CameraXFragment.this, preview, imageCapture);
            }
        });

    }

    @Override
    public BaseOverlayView createOverlayView() {
        CaptureControllerView overlayView = new CaptureControllerView(getContext());
        overlayView.setCameraControllerListener(this);
        return overlayView;
    }


    @Override
    public void captureImage() {

        mCameraManager.capturePhoto(FileUtils.createFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FileUtils.FILENAME, FileUtils.PHOTO_EXTENSION));
    }

    @Override
    public void switchLensFacing() {

    }

    @Override
    public void switchAspectRatio() {

    }


    @Override
    public void onCapturePhotoSuccess(@NonNull File file) {
        resultContainer.setVisibility(View.VISIBLE);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
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
