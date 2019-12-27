package com.ciuciu.camerax.camera;

import android.view.TextureView;

public interface CameraManagerView {

    void setListener(CameraManagerListener listener);

    void onAttach(TextureView textureView);

    void onDetach();

    boolean switchLensFacing();
}
