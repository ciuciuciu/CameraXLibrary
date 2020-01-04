package com.ciuciu.camerax.preview;

import android.content.Context;
import android.graphics.Matrix;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.camera.core.Preview;

import com.ciuciu.camerax.CameraHelper;
import com.ciuciu.camerax.config.PreviewScaleType;

class AutoFitTextureView extends TextureView {

    private static final String TAG = AutoFitTextureView.class.getSimpleName();

    private Size mCurrentSize = new Size(0, 0);
    private int mCurrentRotation = -1;
    private int mDisplayId = -1;

    private Size mPreviewSourceSize = new Size(0, 0);
    private int mPreviewSourceRotation = 0;

    private @PreviewScaleType.ScaleType
    int mCameraScaleType = PreviewScaleType.SCALE_TYPE_CENTER_CROP;

    private DisplayManager mDisplayManager;

    private DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int i) {
        }

        @Override
        public void onDisplayRemoved(int i) {
        }

        @Override
        public void onDisplayChanged(int displayId) {
            if (AutoFitTextureView.this == null) {
                return;
            }
            if (displayId == mDisplayId) {
                updateTransform(null, mPreviewSourceSize, mCurrentSize);
            }
        }
    };

    public AutoFitTextureView(Context context) {
        this(context, null);
        init();
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mDisplayManager = (DisplayManager) getContext().getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(displayListener, null);

        this.post(new Runnable() {
            @Override
            public void run() {
                mDisplayId = getDisplay().getDisplayId();
            }
        });
        this.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int i4, int i5, int i6, int i7) {
                Size newViewFinderDimens = new Size(right - left, bottom - top);
                updateTransform(null, mPreviewSourceSize, newViewFinderDimens);
            }
        });
        this.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                mDisplayManager.registerDisplayListener(displayListener, null);
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                mDisplayManager.unregisterDisplayListener(displayListener);
            }
        });
    }

    public void startPreview(@PreviewScaleType.ScaleType int scaleType) {
        mCameraScaleType = scaleType;
        mCurrentRotation = CameraHelper.getDisplaySurfaceRotation(getDisplay());
    }

    public void updateTransform(@Nullable Preview.PreviewOutput previewSource, Size newPreviewSourceSize, Size newTextureViewSize) {
        if (previewSource != null) {
            mPreviewSourceRotation = previewSource.getRotationDegrees();
        }
        int newTextureViewRotation;
        if (getDisplay() != null) {
            newTextureViewRotation = CameraHelper.getDisplaySurfaceRotation(getDisplay());
        } else {
            newTextureViewRotation = mCurrentRotation;
        }

        if (newTextureViewRotation == mCurrentRotation && newPreviewSourceSize == mPreviewSourceSize && newTextureViewSize == mCurrentSize) {
            // Nothing has changed, no need to transform output again
            return;
        }

        if (newTextureViewRotation < 0) {
            // Invalid rotation - wait for valid inputs before setting matrix
            return;
        } else {
            // Update internal field with new inputs
            mCurrentRotation = newTextureViewRotation;
        }
        if (newPreviewSourceSize.getWidth() == 0 || newPreviewSourceSize.getHeight() == 0) {
            // Invalid buffer dimens - wait for valid inputs before setting matrix
            return;
        } else {
            // Update internal field with new inputs
            mPreviewSourceSize = newPreviewSourceSize;
        }
        if (newTextureViewSize.getWidth() == 0 || newTextureViewSize.getHeight() == 0) {
            // Invalid view finder dimens - wait for valid inputs before setting matrix
            return;
        } else {
            // Update internal field with new inputs
            mCurrentSize = newTextureViewSize;
        }

        Log.d(TAG, "------------------------------------------------------");
        // Compute the center of the TextureView
        float centerX = mCurrentSize.getWidth() / 2f;
        float centerY = mCurrentSize.getHeight() / 2f;

        Matrix matrix = new Matrix();
        // Correct preview output to account for display rotation
        matrix.postRotate(-(float) mCurrentRotation, centerX, centerY);

        // Buffers are rotated relative to the device's 'natural' orientation.
        boolean isNaturalPortrait = mCurrentRotation == 0 || mCurrentRotation == 180;
        Log.d(TAG, "isNaturalPortrait " + isNaturalPortrait);

        int previewSourceWidth;
        int previewSourceHeight;
        if (isNaturalPortrait) {
            previewSourceWidth = mPreviewSourceSize.getHeight();
            previewSourceHeight = mPreviewSourceSize.getWidth();
        } else {
            previewSourceWidth = mPreviewSourceSize.getHeight();
            previewSourceHeight = mPreviewSourceSize.getWidth();
        }

        int bufferRotatedWidth;
        int bufferRotatedHeight;
        if (mCurrentRotation == 0 || mCurrentRotation == 180) {
            bufferRotatedWidth = previewSourceWidth;
            bufferRotatedHeight = previewSourceHeight;
        } else {
            bufferRotatedWidth = previewSourceHeight;
            bufferRotatedHeight = previewSourceWidth;
        }

        // Scale back the buffers back to the original output buffer dimensions.
        float xScale = previewSourceWidth / (float) mCurrentSize.getWidth();
        float yScale = previewSourceHeight / (float) mCurrentSize.getHeight();

        Log.d(TAG, "previewSourceWidth " + previewSourceWidth);
        Log.d(TAG, "previewSourceHeight " + previewSourceHeight);
        Log.d(TAG, "bufferRotatedWidth " + bufferRotatedWidth);
        Log.d(TAG, "bufferRotatedHeight " + bufferRotatedHeight);
        Log.d(TAG, "Current-Width " + mCurrentSize.getWidth());
        Log.d(TAG, "Current-Height " + mCurrentSize.getHeight());
        Log.d(TAG, "PreviewSourceRotation " + mPreviewSourceRotation);
        Log.d(TAG, "mCurrentRotation " + mCurrentRotation);
        Log.d(TAG, "xScale " + xScale);
        Log.d(TAG, "yScale " + yScale);

        float scaleFactor;

        switch (mCameraScaleType) {
            case PreviewScaleType.SCALE_TYPE_FIT_XY:
                float scaleFactorX;
                float scaleFactorY;

                if (mCurrentRotation == 90 || mCurrentRotation == 270) {
                    scaleFactorX = mCurrentSize.getHeight() / (float) bufferRotatedHeight;
                    scaleFactorY = mCurrentSize.getWidth() / (float) bufferRotatedWidth;
                } else {
                    scaleFactorX = mCurrentSize.getWidth() / (float) bufferRotatedWidth;
                    scaleFactorY = mCurrentSize.getHeight() / (float) bufferRotatedHeight;
                }
                xScale *= scaleFactorX;
                yScale *= scaleFactorY;
                matrix.preScale(xScale, yScale, centerX, centerY);
                break;

            case PreviewScaleType.SCALE_TYPE_FIT_CENTER:
                scaleFactor = Math.min(mCurrentSize.getWidth() / (float) bufferRotatedWidth, mCurrentSize.getHeight() / (float) bufferRotatedHeight);
                xScale *= scaleFactor;
                yScale *= scaleFactor;
                matrix.preScale(xScale, yScale, centerX, centerY);
                break;

            case PreviewScaleType.SCALE_TYPE_CENTER_CROP:
                scaleFactor = Math.max(mCurrentSize.getWidth() / (float) bufferRotatedWidth, mCurrentSize.getHeight() / (float) bufferRotatedHeight);
                xScale *= scaleFactor;
                yScale *= scaleFactor;
                matrix.preScale(xScale, yScale, centerX, centerY);
                break;
        }

        this.setTransform(matrix);
    }

    public Size getCurrentSize() {
        return mCurrentSize;
    }
}
