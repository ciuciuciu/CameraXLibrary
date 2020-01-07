package com.ciuciu.camerax.config;

import android.content.Context;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;

import java.text.DecimalFormat;
import java.util.List;

public class CameraConfig {

    private final String TAG = CameraConfig.class.getSimpleName();

    private static final CameraX.LensFacing DEFAULT_LENS_FACING = CameraX.LensFacing.BACK;
    private static final AspectRatio DEFAULT_ASPECT_RATIO = AspectRatio.RATIO_16_9;
    private static final int DEFAULT_TARGET_RESOLUTION_WIDTH = 1920;
    private static final int DEFAULT_TARGET_RESOLUTION_HEIGHT = 1080;

    private Context mContext;

    private CameraX.LensFacing mLensFacing = DEFAULT_LENS_FACING;
    private AspectRatio mAspectRatio = DEFAULT_ASPECT_RATIO;
    private TargetResolution mTargetResolution;
    private PreviewScaleType mPreviewScaleType;

    public static class Builder {
        private CameraX.LensFacing mLensFacing = DEFAULT_LENS_FACING;
        private AspectRatio mAspectRatio = DEFAULT_ASPECT_RATIO;
        private TargetResolution mTargetResolution;
        private PreviewScaleType mPreviewScaleType;

        public Builder setLensFacing(CameraX.LensFacing lensFacing) {
            mLensFacing = lensFacing;
            return this;
        }

        public Builder setAspectRatio(AspectRatio aspectRatio) {
            mAspectRatio = aspectRatio;
            return this;
        }

        public Builder setTargetResolution(TargetResolution targetResolution) {
            mTargetResolution = targetResolution;
            return this;
        }

        public Builder setPreviewScaleType(PreviewScaleType previewScaleType) {
            mPreviewScaleType = previewScaleType;
            return this;
        }

        public CameraConfig build(Context context) {
            return new CameraConfig(context, mLensFacing, mAspectRatio, mTargetResolution, mPreviewScaleType);
        }
    }

    public CameraConfig(Context context,
                        @Nullable CameraX.LensFacing lensFacing,
                        @Nullable AspectRatio aspectRatio,
                        @Nullable TargetResolution targetResolution,
                        @Nullable PreviewScaleType previewScaleType) {

        this.mContext = context;
        if (lensFacing != null) {
            mLensFacing = lensFacing;
        }
        if (aspectRatio != null) {
            mAspectRatio = aspectRatio;
        }
        if (targetResolution == null) {
            mTargetResolution = new TargetResolution(mContext);
        } else {
            mTargetResolution = targetResolution;
        }
        if (previewScaleType == null) {
            mPreviewScaleType = new PreviewScaleType();
        } else {
            mPreviewScaleType = previewScaleType;
        }
    }

    /**
     * Lens Facing
     */
    public CameraX.LensFacing getLensFacing() {
        return mLensFacing;
    }

    public boolean switchLensFacing() {
        mLensFacing = mLensFacing == CameraX.LensFacing.BACK ? CameraX.LensFacing.FRONT : CameraX.LensFacing.BACK;
        return true;
    }

    /**
     * Aspect Ratio
     */
    public AspectRatio getAspectRatio() {
        return mAspectRatio;
    }

    public boolean switchAspectRatio() {
        mAspectRatio = mAspectRatio == AspectRatio.RATIO_16_9 ? AspectRatio.RATIO_4_3 : AspectRatio.RATIO_16_9;
        mTargetResolution.setSize(null);
        return true;
    }

    /**
     * Target Resolution
     */
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
        mTargetResolution.setSize(size);
        return true;
    }

    public String getDisplayMP() {
        Size targetResolution = getTargetResolution(0);
        float pixelOfCamera = (targetResolution.getWidth() * targetResolution.getHeight()) / (float) 1024000;

        int pixelNumber = Math.round(pixelOfCamera);

        DecimalFormat precision = new DecimalFormat("#.#");
        //return precision.format(pixelNumber) + "MP";

        return targetResolution.getWidth() + " x " + targetResolution.getHeight();

    }

    /**
     * CameraPreview output scale type
     */
    public boolean changePreviewScaleType() {
        mPreviewScaleType.changeScaleType();
        return true;
    }

    public int getPreviewScaleType() {
        return mPreviewScaleType.getScaleType();
    }


}
