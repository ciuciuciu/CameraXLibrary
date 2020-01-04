package com.ciuciu.camerax.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.camera.core.AspectRatio;

import com.ciuciu.camerax.R;
import com.ciuciu.camerax.config.CameraConfig;
import com.ciuciu.camerax.config.PreviewScaleType;
import com.ciuciu.camerax.controller.overlay.CropOverlayView;
import com.ciuciu.camerax.controller.overlay.ScanOverlayView;
import com.ciuciu.camerax.utils.ImageLoader;

import java.io.File;

public class CaptureControllerView extends BaseControllerView {

    private RelativeLayout overlayContainer;

    private TextView btnChangeResolution;
    private TextView btnChangeRatio;
    private TextView btnChangePreviewScale;

    private ImageButton btnSwitchCamera;
    private ImageButton btnCapture;
    private ImageButton btnViewPhoto;

    public CaptureControllerView(Context context) {
        super(context);
    }

    public CaptureControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CaptureControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.layout_view_controller_capture, this);

        overlayContainer = findViewById(R.id.overlayContainer);

        btnChangeResolution = findViewById(R.id.btnChangeResolution);
        btnChangeRatio = findViewById(R.id.btnChangeRatio);
        btnChangePreviewScale = findViewById(R.id.btnChangePreviewScale);

        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        btnCapture = findViewById(R.id.btnCapture);
        btnViewPhoto = findViewById(R.id.btnViewPhoto);

        btnChangeResolution.setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.changeResolution();
            }
        });

        btnChangeRatio.setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.switchAspectRatio();
            }
        });

        btnChangePreviewScale.setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.changePreviewScale();
            }
        });

        btnSwitchCamera.setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.switchLensFacing();
            }
        });

        btnCapture.setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.captureImage();
            }
        });

        btnViewPhoto.setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.openImageGallery();
            }
        });
    }

    @Override
    public void initOverlayView() {
        overlayContainer.removeAllViews();

        /*mOverlayView = new CropOverlayView(getContext());
        overlayContainer.addView(mOverlayView);
        overlayContainer.setVisibility(VISIBLE);*/
    }

    @Override
    public void updateCameraConfig(CameraConfig cameraConfig) {
        if (cameraConfig != null) {
            btnChangeResolution.setText(cameraConfig.getDisplayMP());
            btnChangeResolution.setVisibility(VISIBLE);

            if (cameraConfig.getAspectRatio() == AspectRatio.RATIO_16_9) {
                btnChangeRatio.setText("16:9");
                btnChangeRatio.setVisibility(VISIBLE);
            } else if (cameraConfig.getAspectRatio() == AspectRatio.RATIO_4_3) {
                btnChangeRatio.setText("4:3");
                btnChangeRatio.setVisibility(VISIBLE);
            }

            switch (cameraConfig.getPreviewScaleType()) {
                case PreviewScaleType.SCALE_TYPE_FIT_XY:
                    btnChangePreviewScale.setText("FIT_XY");
                    btnChangePreviewScale.setVisibility(VISIBLE);
                    break;
                case PreviewScaleType.SCALE_TYPE_FIT_CENTER:
                    btnChangePreviewScale.setText("CENTER");
                    btnChangePreviewScale.setVisibility(VISIBLE);
                    break;
                case PreviewScaleType.SCALE_TYPE_CENTER_CROP:
                    btnChangePreviewScale.setText("CROP");
                    btnChangePreviewScale.setVisibility(VISIBLE);
                    break;
            }
        }
    }

    public void setGalleryThumbnail(File file) {
        // Remove thumbnail padding
        int padding = (int) getResources().getDimension(R.dimen.stroke_small);
        btnViewPhoto.setPadding(padding, padding, padding, padding);
        // Load thumbnail into circular button using Glide
        ImageLoader.loadCircleImage(btnViewPhoto, file);
    }
}
