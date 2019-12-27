package com.ciuciu.camerax.preview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.camera.core.Preview;

import com.ciuciu.camerax.CameraHelper;

class AutoFitTextureView extends TextureView {

    private static final String TAG = AutoFitTextureView.class.getSimpleName();

    private Size mCurrentSize = new Size(0, 0);
    private int mCurrentRotation = -1;
    private int mDisplayId = -1;

    private Size mPreviewSourceSize = new Size(0, 0);
    private int mPreviewSourceRotation = 0;

    private @CameraScale.ScaleType
    int mCameraScaleType = CameraScale.SCALE_TYPE_CENTER_CROP;

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

    public void startPreview(@CameraScale.ScaleType int scaleType) {
        mCameraScaleType = scaleType;
        mCurrentRotation = CameraHelper.getDisplaySurfaceRotation(getDisplay());
    }

//    public void updateTransform(@Nullable Preview.PreviewOutput previewSource, Size newPreviewSourceSize, Size newTextureViewSize) {
//        if (previewSource != null) {
//            mPreviewSourceRotation = previewSource.getRotationDegrees();
//        }
//        int newTextureViewRotation = CameraHelper.getDisplaySurfaceRotation(getDisplay());
//
//        if (newTextureViewRotation == mCurrentRotation && newPreviewSourceSize == mPreviewSourceSize && newTextureViewSize == mCurrentSize) {
//            // Nothing has changed, no need to transform output again
//            return;
//        }
//
//        if (newTextureViewRotation < 0) {
//            // Invalid rotation - wait for valid inputs before setting matrix
//            return;
//        } else {
//            // Update internal field with new inputs
//            mCurrentRotation = newTextureViewRotation;
//        }
//        if (newPreviewSourceSize.getWidth() == 0 || newPreviewSourceSize.getHeight() == 0) {
//            // Invalid buffer dimens - wait for valid inputs before setting matrix
//            return;
//        } else {
//            // Update internal field with new inputs
//            mPreviewSourceSize = newPreviewSourceSize;
//        }
//        if (newTextureViewSize.getWidth() == 0 || newTextureViewSize.getHeight() == 0) {
//            // Invalid view finder dimens - wait for valid inputs before setting matrix
//            return;
//        } else {
//            // Update internal field with new inputs
//            mCurrentSize = newTextureViewSize;
//        }
//
//        Log.d(TAG,
//                "TextureView size: " + mCurrentSize + ".\n" +
//                        "Preview source size: " + mPreviewSourceSize + "\n" +
//                        "TextureView rotation: " + mCurrentRotation + "\n" +
//                        "Preview source rotation: " + mPreviewSourceRotation);
//
//
//        if (mCurrentRotation == 0 || mCurrentRotation == 180) {
//            calculateTransform_0(mPreviewSourceSize, mCameraScaleType);
//        } else {
//            calculateTransform_90(mPreviewSourceSize, mCurrentRotation, mCameraScaleType);
//        }
//    }


    public void updateTransform(@Nullable Preview.PreviewOutput previewSource, Size newPreviewSourceSize, Size newTextureViewSize) {
        if (previewSource != null) {
            mPreviewSourceRotation = previewSource.getRotationDegrees();
        }
        int newTextureViewRotation;
        if(getDisplay() != null){
            newTextureViewRotation = CameraHelper.getDisplaySurfaceRotation(getDisplay());
        }else {
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

        Log.d(TAG,
                "TextureView size: " + mCurrentSize + ".\n" +
                        "Preview source size: " + mPreviewSourceSize + "\n" +
                        "TextureView rotation: " + mCurrentRotation + "\n" +
                        "Preview source rotation: " + mPreviewSourceRotation);

        Matrix matrix = new Matrix();

        // Compute the center of the TextureView
        float centerX = mCurrentSize.getWidth() / 2f;
        float centerY = mCurrentSize.getHeight() / 2f;

        // Correct preview output to account for display rotation
        matrix.postRotate(-(float) mCurrentRotation, centerX, centerY);

        // Buffers are rotated relative to the device's 'natural' orientation.
        boolean isNaturalPortrait = ((mCurrentRotation == 0 || mCurrentRotation == 180) &&
                mCurrentSize.getWidth() < mCurrentSize.getHeight())
                || ((mCurrentRotation == 90 || mCurrentRotation == 270) &&
                mCurrentSize.getWidth() >= mCurrentSize.getHeight());
        Log.d(TAG, "isNaturalPortrait " + isNaturalPortrait);

        int previewSourceWidth;
        int previewSourceHeight;
        if (isNaturalPortrait) {
            previewSourceWidth = mPreviewSourceSize.getHeight();
            previewSourceHeight = mPreviewSourceSize.getWidth();
        } else {
            previewSourceWidth = mPreviewSourceSize.getWidth();
            previewSourceHeight = mPreviewSourceSize.getHeight();
        }

        // Scale back the buffers back to the original output buffer dimensions.
        float xScale = previewSourceWidth / (float) mCurrentSize.getWidth();
        float yScale = previewSourceHeight / (float) mCurrentSize.getHeight();

        int bufferRotatedWidth;
        int bufferRotatedHeight;

        if (mCurrentRotation == 0 || mCurrentRotation == 180) {
            bufferRotatedWidth = previewSourceWidth;
            bufferRotatedHeight = previewSourceHeight;
        } else {
            bufferRotatedWidth = previewSourceHeight;
            bufferRotatedHeight = previewSourceWidth;
        }

//        Log.d(TAG, "previewSourceWidth " + previewSourceWidth);
//        Log.d(TAG, "previewSourceHeight " + previewSourceHeight);
//        Log.d(TAG, "bufferRotatedWidth " + bufferRotatedWidth);
//        Log.d(TAG, "bufferRotatedHeight " + bufferRotatedHeight);
//        Log.d(TAG, "Current-Width " + mCurrentSize.getWidth());
//        Log.d(TAG, "Current-Height " + mCurrentSize.getHeight());
//        Log.d(TAG, "xScale " + xScale);
//        Log.d(TAG, "yScale " + yScale);

        // Scale the buffer so that it just covers the viewfinder.
        if (mCameraScaleType == CameraScale.SCALE_TYPE_CENTER_CROP) {
            float scale = Math.max(mCurrentSize.getWidth() / (float) bufferRotatedWidth,
                    mCurrentSize.getHeight() / (float) bufferRotatedHeight);

            xScale *= scale;
            yScale *= scale;
        } else if (mCameraScaleType == CameraScale.SCALE_TYPE_FIT_CENTER) {
            float scale = Math.min(mCurrentSize.getWidth() / (float) bufferRotatedWidth,
                    mCurrentSize.getHeight() / (float) bufferRotatedHeight);

            xScale *= scale;
            yScale *= scale;
        } else {
            float scale = Math.max(mCurrentSize.getWidth() / (float) bufferRotatedWidth,
                    mCurrentSize.getHeight() / (float) bufferRotatedHeight);

            xScale *= scale;
            yScale *= scale;
        }

        // Scale input buffers to fill the view finder
        matrix.preScale(xScale, yScale, centerX, centerY);

        // Finally, apply transformations to our TextureView
        this.setTransform(matrix);
    }

    public Size getCurrentSize() {
        return mCurrentSize;
    }

    /**
     * Helper function that fits a camera preview into the given [TextureView]
     */
//    private int mCurrentRotation;
//    private Size sizePreviewSource = new Size(0, 0);
//
//    public void updateTransform(Size sizePreview, int newRotation, @CameraScale.ScaleType int scaleType, int sourcePreviewRotation) {
//        if (newRotation == mCurrentRotation && sizePreview == sizePreviewSource) {
//            // Nothing has changed, no need to transform output again
//            return;
//        }
//
//        if (newRotation < 0) {
//            // Invalid rotation - wait for valid inputs before setting matrix
//            return;
//        } else {
//            // Update internal field with new inputs
//            mCurrentRotation = newRotation;
//        }
//
//        if (sizePreview.getWidth() == 0 || sizePreview.getHeight() == 0) {
//            // Invalid view finder dimens - wait for valid inputs before setting matrix
//            return;
//        } else {
//            // Update internal field with new inputs
//            sizePreviewSource = sizePreview;
//        }
//
//        Log.d(TAG, "Applying output transformation.\n" +
//                "View finder size: " + new Size(this.getWidth(), this.getHeight()) + ".\n" +
//                "Preview output size: " + sizePreview + ".\n" +
//                "Rotation: " + mCurrentRotation + ".\n" +
//                "SourcePreviewRotation" + sourcePreviewRotation);
//
//        if (mCurrentRotation == 0) {
//            calculateTransform_0(sizePreview, scaleType);
//            return;
//        }
//
//        if (mCurrentRotation == 270 || mCurrentRotation == 90) {
//            calculateTransform_90(sizePreview, mCurrentRotation, scaleType);
//            return;
//        }
//    }
//
    private void calculateTransform_0(Size sizePreview, @CameraScale.ScaleType int scaleType) {
        if (scaleType == CameraScale.SCALE_TYPE_FIT_XY) {
            //return;
        }

        int width = getWidth();
        int height = getHeight();

        RectF viewRect = new RectF(0, 0, width, height);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        RectF bufferRect = new RectF(0, 0, sizePreview.getHeight(), sizePreview.getWidth());
        bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());

        Matrix matrix = new Matrix();
        matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
        float scale;
        if (scaleType == CameraScale.SCALE_TYPE_FIT_CENTER) {
            float scaleWidth = (float) width / sizePreview.getHeight();
            float scaleHeight = (float) height / sizePreview.getWidth();
            scale = Math.min(scaleWidth, scaleHeight);
            matrix.postScale(scale, scale, centerX, centerY);

        } else if (scaleType == CameraScale.SCALE_TYPE_CENTER_CROP) {
            float scaleWidth = (float) width / sizePreview.getHeight();
            float scaleHeight = (float) height / sizePreview.getWidth();

            scale = Math.max(scaleWidth, scaleHeight);
            matrix.postScale(scale, scale, centerX, centerY);
        }

        this.setTransform(matrix);
    }

    private void calculateTransform_90(Size sizePreview, int rotation, @CameraScale.ScaleType int scaleType) {
//        RectF viewRect = new RectF(0, 0, getHeight(), getWidth());
//        float centerX = viewRect.centerX();
//        float centerY = viewRect.centerY();
//
//        RectF bufferRect = new RectF(0, 0, sizePreview.getWidth(), sizePreview.getHeight());
//        //bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
//
//        Matrix matrix = new Matrix();
//
//        matrix.setRectToRect(bufferRect, viewRect, Matrix.ScaleToFit.FILL);
//        matrix.postRotate(-rotation ,centerY,centerY );
//
//        //matrix.postRotate(rotation - 180, viewRect.centerX(), viewRect.centerY() );
//
//
//
//        float scale;
//
//        float scaleX = (float) getWidth() / sizePreview.getHeight();
//        float scaleY = (float) getHeight() / sizePreview.getWidth();
//
//        if (scaleType == CameraScale.SCALE_TYPE_FIT_CENTER) {
//
//
//            scale = Math.min(
//                    scaleX,
//                    scaleY);
//
//            Log.d(TAG, "scaleX " + scaleX);
//            Log.d(TAG, "scaleY " + scaleY);
//            //matrix.postScale(scaleX, scaleY, centerX, centerY);
//
//
//        } else if (scaleType == CameraScale.SCALE_TYPE_CENTER_CROP) {
//            scale = Math.max(
//                    (float) getHeight() / sizePreview.getHeight(),
//                    (float) getWidth() / sizePreview.getWidth());
//            matrix.postScale(scale, scale, centerX, centerY);
//        } else {
//
//
//
//            matrix.postScale( scaleX,scaleY, centerX, centerY);
//        }
//
//
//
//
//        this.setTransform(matrix);
        Matrix matrix = new Matrix();

        // Compute the center of the TextureView
        float centerX = mCurrentSize.getWidth() / 2f;
        float centerY = mCurrentSize.getHeight() / 2f;

        // Correct preview output to account for display rotation
        matrix.postRotate(-(float) mCurrentRotation, centerX, centerY);

        // Buffers are rotated relative to the device's 'natural' orientation.
        boolean isNaturalPortrait = ((mCurrentRotation == 0 || mCurrentRotation == 180) &&
                mCurrentSize.getWidth() < mCurrentSize.getHeight())
                || ((mCurrentRotation == 90 || mCurrentRotation == 270) &&
                mCurrentSize.getWidth() >= mCurrentSize.getHeight());
        Log.d(TAG, "isNaturalPortrait " + isNaturalPortrait);

        int previewSourceWidth;
        int previewSourceHeight;
        if (isNaturalPortrait) {
            previewSourceWidth = mPreviewSourceSize.getHeight();
            previewSourceHeight = mPreviewSourceSize.getWidth();
        } else {
            previewSourceWidth = mPreviewSourceSize.getWidth();
            previewSourceHeight = mPreviewSourceSize.getHeight();
        }

        // Scale back the buffers back to the original output buffer dimensions.
        float xScale = previewSourceWidth / (float) mCurrentSize.getWidth();
        float yScale = previewSourceHeight / (float) mCurrentSize.getHeight();

        int bufferRotatedWidth;
        int bufferRotatedHeight;

        if (mCurrentRotation == 0 || mCurrentRotation == 180) {
            bufferRotatedWidth = previewSourceWidth;
            bufferRotatedHeight = previewSourceHeight;
        } else {
            bufferRotatedWidth = previewSourceHeight;
            bufferRotatedHeight = previewSourceWidth;
        }

         //Scale the buffer so that it just covers the viewfinder.
        if (mCameraScaleType == CameraScale.SCALE_TYPE_CENTER_CROP) {
            float scale = Math.max(mCurrentSize.getWidth() / (float) bufferRotatedWidth,
                    mCurrentSize.getHeight() / (float) bufferRotatedHeight);

            xScale *= scale;
            yScale *= scale;
        } else if (mCameraScaleType == CameraScale.SCALE_TYPE_FIT_CENTER) {
            float scale = Math.min(mCurrentSize.getWidth() / (float) bufferRotatedWidth,
                    mCurrentSize.getHeight() / (float) bufferRotatedHeight);

            xScale *= scale;
            yScale *= scale;
        } else {
            xScale *= mCurrentSize.getHeight() / (float) bufferRotatedHeight;
            yScale *= mCurrentSize.getWidth() / (float) bufferRotatedHeight;
        }

        // Scale input buffers to fill the view finder
        matrix.preScale(xScale, yScale, centerX, centerY);

        this.setTransform(matrix);

    }
}
