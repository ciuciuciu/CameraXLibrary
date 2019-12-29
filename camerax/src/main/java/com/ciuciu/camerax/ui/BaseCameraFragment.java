package com.ciuciu.camerax.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ciuciu.camerax.CameraHelper;

public abstract class BaseCameraFragment extends Fragment {

    /**
     * Milliseconds used for UI animations
     */
    public final long ANIMATION_FAST_MILLIS = 50L;
    public final long ANIMATION_SLOW_MILLIS = 100L;

    private final String TAG = BaseCameraFragment.class.getSimpleName();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CameraHelper.PERMISSION_REQUEST_CODE) {
            if (CameraHelper.isAllPermissionGranted(getContext())) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                openCamera();
            } else {
                Toast.makeText(getContext(), "ERROR: Camera permissions not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragment(view);
    }

    protected abstract void initFragment(@NonNull View view);

    public abstract void openCamera();

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    protected void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}