package com.ciuciu.camerax.controller.overlay;

import android.graphics.Rect;

public class Frame {

    private float left;
    private float right;
    private float top;
    private float bottom;

    private float relativePosX;
    private float relativePosY;
    private float relativeWidth;
    private float relativeHeight;

    public Rect toRect() {
        return new Rect((int) left, (int) top, (int) right, (int) bottom);
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public float getTop() {
        return top;
    }

    public float getBottom() {
        return bottom;
    }

    public float getWidth() {
        return right - left;
    }

    public float getHeight() {
        return bottom - top;
    }

    public float getRelativePosX() {
        return relativePosX;
    }

    public float getRelativePosY() {
        return relativePosY;
    }

    public float getRelativeWidth() {
        return relativeWidth;
    }

    public float getRelativeHeight() {
        return relativeHeight;
    }

    public static class Builder {
        private float width;
        private float height;

        private float left;
        private float right;
        private float top;
        private float bottom;

        private float relativePosX;
        private float relativePosY;
        private float relativeWidth;
        private float relativeHeight;

        public Builder widthSize(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder fromPosition(float left, float right, float top, float bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;

            this.relativePosX = this.left / this.width;
            this.relativePosY = this.top / this.height;
            this.relativeWidth = (this.right - this.left) / this.width;
            this.relativeHeight = (this.bottom - this.top) / this.height;
            return this;
        }

        public Builder fromRelative(float relativePosX, float relativePosY, float relativeWidth, float relativeHeight) {
            this.relativePosX = relativePosX;
            this.relativePosY = relativePosY;
            this.relativeWidth = relativeWidth;
            this.relativeHeight = relativeHeight;

            this.left = this.relativePosX * this.width;
            this.right = this.left + (this.relativeWidth * this.width);
            this.top = this.relativePosY * this.height;
            this.bottom = this.top + (this.relativeHeight * this.height);
            return this;
        }

        public Frame build() {
            Frame frame = new Frame();

            frame.left = this.left;
            frame.right = this.right;
            frame.top = this.top;
            frame.bottom = this.bottom;

            frame.relativePosX = this.relativePosX;
            frame.relativePosY = this.relativePosY;
            frame.relativeWidth = this.relativeWidth;
            frame.relativeHeight = this.relativeHeight;

            return frame;
        }
    }
}
