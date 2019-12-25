package com.ciuciu.cameralibrary.preview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class AutoFitTextureView extends TextureView {

    private static final String TAG = AutoFitTextureView.class.getSimpleName();

    private int mCurrentRotation;
    private Size sizePreviewSource = new Size(0, 0);

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Helper function that fits a camera preview into the given [TextureView]
     */
    public void updateTransform(Size sizePreview, int newRotation, @CameraScale.ScaleType int scaleType, int sourcePreviewRotation) {
        if (newRotation == mCurrentRotation && sizePreview == sizePreviewSource) {
            // Nothing has changed, no need to transform output again
            return;
        }

        if (newRotation < 0) {
            // Invalid rotation - wait for valid inputs before setting matrix
            return;
        } else {
            // Update internal field with new inputs
            mCurrentRotation = newRotation;
        }

        if (sizePreview.getWidth() == 0 || sizePreview.getHeight() == 0) {
            // Invalid view finder dimens - wait for valid inputs before setting matrix
            return;
        } else {
            // Update internal field with new inputs
            sizePreviewSource = sizePreview;
        }

        Log.d(TAG, "Applying output transformation.\n" +
                "View finder size: " + new Size(this.getWidth(), this.getHeight()) + ".\n" +
                "Preview output size: " + sizePreview + ".\n" +
                "Rotation: " + mCurrentRotation + ".\n" +
                "SourcePreviewRotation" + sourcePreviewRotation);

        if (mCurrentRotation == 0) {
            calculateTransform_0(sizePreview, scaleType);
            return;
        }

        if (mCurrentRotation == 270 || mCurrentRotation == 90) {
            calculateTransform_90(sizePreview, mCurrentRotation, scaleType);
            return;
        }
    }

    private void calculateTransform_0(Size sizePreview, @CameraScale.ScaleType int scaleType) {
        if (scaleType == CameraScale.SCALE_TYPE_FIT_XY) {
            return;
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
        if (scaleType == CameraScale.SCALE_TYPE_FIT_XY) {
            return;
        }

        RectF viewRect = new RectF(0, 0, getWidth(), getHeight());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        RectF bufferRect = new RectF(0, 0, sizePreview.getHeight(), sizePreview.getWidth());
        bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());

        Matrix matrix = new Matrix();
        matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
        float scale;
        if (scaleType == CameraScale.SCALE_TYPE_FIT_CENTER) {
            scale = Math.min(
                    (float) getHeight() / sizePreview.getHeight(),
                    (float) getWidth() / sizePreview.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);

        } else if (scaleType == CameraScale.SCALE_TYPE_CENTER_CROP) {
            scale = Math.max(
                    (float) getHeight() / sizePreview.getHeight(),
                    (float) getWidth() / sizePreview.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
        }

        matrix.postRotate(rotation - 180, centerX, centerY);
        this.setTransform(matrix);
    }
}