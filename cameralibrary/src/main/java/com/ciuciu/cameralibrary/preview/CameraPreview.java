package com.ciuciu.cameralibrary.preview;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.camera.core.Preview;

import com.ciuciu.cameralibrary.CameraHelper;
import com.ciuciu.cameralibrary.R;
import com.ciuciu.cameralibrary.overlaycontroller.BaseOverlayView;

public class CameraPreview extends RelativeLayout {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private ViewGroup mTextureViewContainer;
    private AutoFitTextureView mTextureView;

    private RelativeLayout mOverlayContainer;
    private BaseOverlayView mOverlayView;

    private Preview mPreview;

    private @CameraScale.ScaleType
    int mCameraScaleType = CameraScale.SCALE_TYPE_FIT_CENTER;

    private DisplayManager displayManager;

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        displayManager.unregisterDisplayListener(displayListener);
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.view_layout_camera_preview, this);

        mTextureViewContainer = findViewById(R.id.textureViewContainer);
        mTextureView = findViewById(R.id.textureView);
        mOverlayContainer = findViewById(R.id.overlayContainer);

        displayManager = (DisplayManager) getContext().getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(displayListener, null);
    }

    public void setOverlayView(BaseOverlayView controllerView) {
        mOverlayView = controllerView;
        mOverlayContainer.removeAllViews();
        mOverlayContainer.addView(mOverlayView);
        mOverlayContainer.setVisibility(VISIBLE);
    }

    public void setPreview(Preview preview) {
        mPreview = preview;
        mPreview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput output) {
                        // To update the SurfaceTexture, we have to remove it and re-add it
                        mTextureViewContainer.removeAllViews();
                        mTextureViewContainer.addView(mTextureView, 0);
                        // Update internal texture
                        mTextureView.setSurfaceTexture(output.getSurfaceTexture());
                        // Apply relevant transformations
                        int rotation = CameraHelper.getSurfaceDisplayRotation(mTextureView.getDisplay());
                        mTextureView.updateTransform(output.getTextureSize(), rotation, mCameraScaleType, output.getRotationDegrees());
                    }
                });
    }

    public float getRotation(){
        return mTextureView.getDisplay().getRotation();
    }

    private DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int i) {

        }

        @Override
        public void onDisplayRemoved(int i) {

        }

        @Override
        public void onDisplayChanged(int i) {
            if (mPreview != null && mTextureView.getDisplay() != null) {
                mPreview.setTargetRotation(mTextureView.getDisplay().getRotation());
                Log.d(TAG, "DisplayManager onDisplayChanged");
            }
        }
    };
}
