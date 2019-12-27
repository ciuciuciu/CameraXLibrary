package com.ciuciu.camerax.camera.config;

import android.content.Context;
import android.util.Size;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;

import java.text.DecimalFormat;
import java.util.List;

public class CameraConfig {

    private final String TAG = CameraConfig.class.getSimpleName();

    private final CameraX.LensFacing DEFAULT_LENS_FACING = CameraX.LensFacing.BACK;
    private final AspectRatio DEFAULT_ASPECT_RATIO = AspectRatio.RATIO_16_9;
    private final int DEFAULT_TARGET_RESOLUTION_WIDTH = 1920;
    private final int DEFAULT_TARGET_RESOLUTION_HEIGHT = 1080;
    /**
     * Enable switch Back camera or Front camera
     */
    private final boolean ENABLE_SWITCH_LENS_FACING = true;
    /**
     * Enable switch camera aspect ration between AspectRatio.RATIO_16_9 or AspectRatio.RATIO_4_3
     */
    private final boolean ENABLE_CHANGE_ASPECT_RATIO = true;
    /**
     * Enable change camera target resolution
     */
    private final boolean ENABLE_CHANGE_TARGET_RESOLUTION = true;

    private Context mContext;

    private CameraX.LensFacing mLensFacing = DEFAULT_LENS_FACING;
    private AspectRatio mAspectRatio = DEFAULT_ASPECT_RATIO;
    private TargetResolution mTargetResolution;

    public CameraConfig(Context context) {
        mContext = context;
        mTargetResolution = new TargetResolution(mContext);
    }

    /**
     * Lens Facing
     */
    public boolean isEnableSwitchLensFacing() {
        return ENABLE_SWITCH_LENS_FACING;
    }

    public CameraX.LensFacing getLensFacing() {
        return mLensFacing;
    }

    public boolean switchLensFacing() {
        if (ENABLE_SWITCH_LENS_FACING) {
            mLensFacing = mLensFacing == CameraX.LensFacing.BACK ? CameraX.LensFacing.FRONT : CameraX.LensFacing.BACK;
            return true;
        }
        return false;
    }

    /**
     * Aspect Ratio
     */

    public boolean isEnableSwitchAspectRatio() {
        return ENABLE_CHANGE_ASPECT_RATIO;
    }

    public AspectRatio getAspectRatio() {
        return mAspectRatio;
    }

    public boolean switchAspectRatio() {
        if (ENABLE_CHANGE_ASPECT_RATIO) {
            mAspectRatio = mAspectRatio == AspectRatio.RATIO_16_9 ? AspectRatio.RATIO_4_3 : AspectRatio.RATIO_16_9;
            return true;
        }
        return false;
    }

    /**
     * Target Resolution
     */

    public boolean isEnableChangeTargetResolution() {
        return ENABLE_CHANGE_TARGET_RESOLUTION;
    }

    public Size getDefaultTargetResolution(int rotation) {
        if (rotation == 90 || rotation == 270) {
            return new Size(DEFAULT_TARGET_RESOLUTION_WIDTH, DEFAULT_TARGET_RESOLUTION_HEIGHT);
        }
        return new Size(DEFAULT_TARGET_RESOLUTION_HEIGHT, DEFAULT_TARGET_RESOLUTION_WIDTH);
    }

    public List<Size> getSupportedResolution() {
        return mTargetResolution.generateSupportedResolution(mAspectRatio, mLensFacing);
    }

    public Size getTargetResolution(int rotation) {
        return mTargetResolution.getTargetResolution(mAspectRatio, mLensFacing, rotation);
    }

    public boolean setTargetResolution(Size size) {
        if (ENABLE_CHANGE_TARGET_RESOLUTION) {
            mTargetResolution.setSize(size);
            return true;
        }
        return false;
    }

    public String getDisplayMP() {
        Size targetResolution = getTargetResolution(0);
        float pixelOfCamera = (targetResolution.getWidth() * targetResolution.getHeight()) / (float) 1024000;

        int pixelNumber = Math.round(pixelOfCamera);

        DecimalFormat precision = new DecimalFormat("#.#");
        //return precision.format(pixelNumber) + "MP";

        return targetResolution.getWidth() + " x " + targetResolution.getHeight();

    }
}
