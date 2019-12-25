package com.ciuciu.cameralibrary.preview;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CameraScale {

    public static final int SCALE_TYPE_FIT_XY = 0;
    public static final int SCALE_TYPE_FIT_CENTER = 1;
    public static final int SCALE_TYPE_CENTER_CROP = 2;

    @IntDef({SCALE_TYPE_FIT_XY, SCALE_TYPE_FIT_CENTER, SCALE_TYPE_CENTER_CROP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScaleType {
    }
}
