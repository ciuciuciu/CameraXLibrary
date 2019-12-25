package com.ciuciu.camerax.camera;

import android.view.Display;

public interface CameraManagerView {

    void setListener(CameraManagerListener listener);

    void onAttach(Display display);

    void onDetach();
}
