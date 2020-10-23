package com.ciuciu.camerax.controller.overlay;

import android.content.Context;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
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

    public Frame getOutputTransformFrame(@NonNull Size imageSize) {
        float width = getWidth();
        float height = getHeight();

        if (mInnerFrame == null || imageSize.getWidth() == 0 || imageSize.getHeight() == 0 || width == 0 || height == 0) {
            return null;
        }

        float left = mInnerFrame.getLeft() * imageSize.getWidth() / width;
        float right = mInnerFrame.getRight() * imageSize.getWidth() / width;
        float top = mInnerFrame.getTop() * imageSize.getHeight() / height;
        float bottom = mInnerFrame.getBottom() * imageSize.getHeight() / height;

        return new Frame(left, right, top, bottom);
    }

    public float getRelativePosX() {
        return mInnerFrame.getLeft() / getWidth();
    }

    public float getRelativePosY() {
        return mInnerFrame.getTop() / getHeight();
    }

    public float getRelativePosWidth() {
        return (mInnerFrame.getRight() - mInnerFrame.getLeft()) / getWidth();
    }

    public float getRelativePosHeight() {
        return (mInnerFrame.getBottom() - mInnerFrame.getTop()) / getHeight();
    }
}
