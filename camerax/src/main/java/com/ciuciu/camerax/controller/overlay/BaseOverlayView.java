package com.ciuciu.camerax.controller.overlay;

import android.content.Context;
import android.view.View;

import androidx.camera.core.ImageProxy;

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

    public Frame getOutputTransformFrame(ImageProxy image, int rotationDegrees) {
        int width = getWidth();
        int height = getHeight();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        if (width <= 0 || height <= 0 || imageWidth <= 0 || imageHeight <= 0) {
            return null;
        }

        float dLeft = mInnerFrame.getLeft() / width;
        float dTop = mInnerFrame.getTop() / height;
        float dRight = mInnerFrame.getRight() / width;
        float dBottom = mInnerFrame.getBottom() / height;

        switch (rotationDegrees) {
            case 0:
            case 180:
                return new Frame(dLeft * imageWidth,
                        dRight * imageWidth,
                        dTop * imageHeight,
                        dBottom * imageHeight);

            case 90:
                return new Frame(
                        dTop * imageWidth,
                        dBottom * imageWidth,
                        dLeft * imageHeight,
                        dRight * imageHeight);

            case 270:
                return new Frame(
                        (1f - dBottom) * imageWidth,
                        dTop * imageWidth,
                        dLeft * imageHeight,
                        dRight * imageHeight);
        }

        return null;
    }

}
