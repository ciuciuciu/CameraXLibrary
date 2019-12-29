package com.ciuciu.camerax.config;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Log;
import android.util.Size;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;

import com.ciuciu.camerax.utils.CompareSizesByArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class TargetResolution {

    private final String TAG = TargetResolution.class.getSimpleName();

    private final int MAX_WIDTH_RESOLUTION = 4032;
    private final int MAX_HEIGHT_RESOLUTION = 3024;
    private final int MIN_WIDTH_RESOLUTION = 640;
    private final int MIN_HEIGHT_RESOLUTION = 480;

    private Context mContext;
    private android.hardware.camera2.CameraManager mCameraManager;

    private Size size;

    public TargetResolution(Context context) {
        mContext = context;
        mCameraManager = (android.hardware.camera2.CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Size getTargetResolution(AspectRatio aspectRatio, CameraX.LensFacing lensFacing, int rotation) {
        if (size != null) {
            if (rotation == 1 || rotation == 3) {
                return new Size(size.getWidth(), size.getHeight());
            } else {
                return new Size(size.getHeight(), size.getWidth());
            }
        }

        List<Size> supportedResolution = generateSupportedResolution(aspectRatio, lensFacing);
        if (supportedResolution == null || supportedResolution.size() == 0) {
            return null;
        }

        int width = supportedResolution.get(0).getWidth();
        int height = supportedResolution.get(0).getHeight();

        if (rotation == 1 || rotation == 3) {
            return new Size(width, height);
        } else {
            return new Size(height, width);
        }
    }

    public List<Size> generateSupportedResolution(AspectRatio aspectRatio, CameraX.LensFacing lensFacing) {
        boolean isBackCamera = lensFacing == CameraX.LensFacing.BACK;

        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);

                Integer cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cameraDirection != null &&
                        isBackCamera != (cameraDirection == CameraCharacteristics.LENS_FACING_BACK)) {
                    continue;
                }
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                List<Size> outputSize = Arrays.asList(map.getOutputSizes(ImageFormat.JPEG));
                Collections.sort(outputSize, new CompareSizesByArea());

                List<Size> result = chooseOptimalSize(aspectRatio, outputSize);
                Log.d(TAG, "output result" + result.size());
                return result;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Size> chooseOptimalSize(AspectRatio aspectRatio, List<Size> choices) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();

        Size sizeRatio = new Size(1, 1);
        if (aspectRatio == AspectRatio.RATIO_16_9) {
            sizeRatio = new Size(16, 9);
        } else if (aspectRatio == AspectRatio.RATIO_4_3) {
            sizeRatio = new Size(4, 3);
        }

        int optionWidth;
        int optionHeight;
        for (Size option : choices) {
            optionWidth = option.getWidth();
            optionHeight = option.getHeight();
            if (optionWidth * sizeRatio.getHeight() == optionHeight * sizeRatio.getWidth()) {
                if (optionWidth <= MAX_WIDTH_RESOLUTION && optionHeight <= MAX_HEIGHT_RESOLUTION &&
                        optionWidth >= MIN_WIDTH_RESOLUTION && optionHeight >= MIN_HEIGHT_RESOLUTION) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the largest of those not big enough.
        if (bigEnough.size() > 0) {
            return bigEnough;
        } else if (notBigEnough.size() > 0) {
            return notBigEnough;
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices;
        }
    }
}