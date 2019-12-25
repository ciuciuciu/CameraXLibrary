package com.ciuciu.cameralibrary.overlaycontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;

import com.ciuciu.cameralibrary.R;

public class CaptureControllerView extends BaseOverlayView {

    private ImageButton btnCapture;

    private CameraControllerListener mControllerListener;

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
    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.layout_controller_capture, this);

        btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(view -> {
            if (mControllerListener != null) {
                mControllerListener.captureImage();
            }
        });
    }

    public void setCameraControllerListener(CameraControllerListener controllerListener) {
        mControllerListener = controllerListener;
    }

}
