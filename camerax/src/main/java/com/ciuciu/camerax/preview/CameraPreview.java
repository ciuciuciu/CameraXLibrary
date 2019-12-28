package com.ciuciu.camerax.preview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.camera.core.Preview;

import com.ciuciu.camerax.R;
import com.ciuciu.camerax.camera.config.PreviewScaleType;
import com.ciuciu.camerax.overlaycontroller.BaseControllerView;

public class CameraPreview extends RelativeLayout {

    private final String TAG = CameraPreview.class.getSimpleName();

    private ViewGroup mTextureViewContainer;
    private AutoFitTextureView mTextureView;

    private RelativeLayout mOverlayContainer;
    private BaseControllerView mOverlayView;

    private Preview mSourcePreview;

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

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.view_layout_camera_preview, this);

        mTextureViewContainer = findViewById(R.id.textureViewContainer);
        mTextureView = findViewById(R.id.textureView);
        mOverlayContainer = findViewById(R.id.overlayContainer);
    }

    public TextureView getTextureView() {
        return mTextureView;
    }

    public void setPreview(Preview preview, @PreviewScaleType.ScaleType int previewScale) {
        mSourcePreview = preview;
        mTextureView.startPreview(previewScale);

        mSourcePreview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(@NonNull Preview.PreviewOutput output) {
                if (mTextureView == null) {
                    return;
                }

                mTextureViewContainer.removeView(mTextureView);
                mTextureViewContainer.addView(mTextureView, 0);
                // Update internal texture
                mTextureView.setSurfaceTexture(output.getSurfaceTexture());

                // Apply relevant transformations
                mTextureView.updateTransform(output, output.getTextureSize(), mTextureView.getCurrentSize());
            }
        });
    }

    public void setOverlayView(BaseControllerView controllerView) {
        mOverlayView = controllerView;
        mOverlayContainer.removeAllViews();
        mOverlayContainer.setVisibility(GONE);

        if (mOverlayView != null) {
            mOverlayContainer.addView(mOverlayView);
            mOverlayContainer.setVisibility(VISIBLE);
        }
    }
}
