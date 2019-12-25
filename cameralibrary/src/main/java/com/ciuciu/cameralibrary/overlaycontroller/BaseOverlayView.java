package com.ciuciu.cameralibrary.overlaycontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class BaseOverlayView extends RelativeLayout {

    public BaseOverlayView(Context context) {
        super(context);
        init();
    }

    public BaseOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public abstract void init();





}
