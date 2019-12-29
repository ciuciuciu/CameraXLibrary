package com.ciuciu.camerax.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ciuciu.camerax.config.CameraConfig;

public abstract class BaseControllerView extends RelativeLayout {

    protected CameraControllerListener mControllerListener;

    public BaseControllerView(Context context) {
        super(context);
        init();
    }

    public BaseControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
    }

    public abstract void initView();

    public abstract void updateCameraConfig(CameraConfig cameraConfig);

    public void setControllerListener(CameraControllerListener controllerListener) {
        mControllerListener = controllerListener;
    }
}
