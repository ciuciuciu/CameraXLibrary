package com.ciuciu.camerax.camera;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;

public class CameraConfig {

    private CameraX.LensFacing mLensFacing = CameraX.LensFacing.BACK;
    private AspectRatio mAspectRatio = AspectRatio.RATIO_16_9;
    // Change Resolution

    public CameraConfig() {

    }

    public CameraConfig(CameraX.LensFacing mLensFacing, AspectRatio mAspectRatio) {
        this.mLensFacing = mLensFacing;
        this.mAspectRatio = mAspectRatio;
    }

    public CameraX.LensFacing getLensFacing() {
        return mLensFacing;
    }

    public void switchLensFacing() {
        mLensFacing = mLensFacing == CameraX.LensFacing.BACK ? CameraX.LensFacing.FRONT : CameraX.LensFacing.BACK;
    }

    public AspectRatio getAspectRatio() {
        return mAspectRatio;
    }

    public void setAspectRatio(AspectRatio mAspectRatio) {
        this.mAspectRatio = mAspectRatio;
    }
}
