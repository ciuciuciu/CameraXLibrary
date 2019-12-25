package com.ciuciu.camerax.overlaycontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;

import com.ciuciu.camerax.R;
import com.ciuciu.camerax.utils.ImageLoader;

import java.io.File;

public class CaptureControllerView extends BaseControllerView {

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

        btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.captureImage();
            }
        });

        findViewById(R.id.btnSwitchCamera).setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.switchLensFacing();
            }
        });

        btnViewPhoto = findViewById(R.id.btnViewPhoto);
        btnViewPhoto.setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.openImageGallery();
            }
        });
    }

    public void setGalleryThumbnail(File file) {
        // Remove thumbnail padding
        int padding = (int) getResources().getDimension(R.dimen.stroke_small);
        btnViewPhoto.setPadding(padding, padding, padding, padding);
        // Load thumbnail into circular button using Glide
        ImageLoader.loadImage(btnViewPhoto, file);
    }
}
