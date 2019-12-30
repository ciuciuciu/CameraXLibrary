package com.ciuciu.camerax.controller.overlay;

import android.content.Context;
import android.view.View;

public abstract class BaseOverlayView extends View {

    protected Frame mOuterFrame;
    protected Frame mInnerFrame;

    public BaseOverlayView(Context context) {
        super(context);
        init();
    }

    public abstract void init();

    public abstract void createFrame(int width, int height);

    public Frame getOuterFrame() {
        return mOuterFrame;
    }

    public Frame getInnerFrame() {
        return mInnerFrame;
    }
}
