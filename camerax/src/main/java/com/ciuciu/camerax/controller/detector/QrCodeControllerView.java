package com.ciuciu.camerax.controller.detector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.ciuciu.camerax.config.CameraConfig;
import com.ciuciu.camerax.controller.BaseControllerView;

public class QrCodeControllerView extends BaseControllerView {


    public QrCodeControllerView(Context context) {
        super(context);
    }

    public QrCodeControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QrCodeControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
    }

    @Override
    public void updateCameraConfig(CameraConfig cameraConfig) {

    }
}
