package com.ciuciu.camerax.controller.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class CropOverlayView extends BaseOverlayView {

    private final float BORDER_STROKE_WIDTH = 12;

    private Bitmap mBitmap;
    private Paint mBitmapPaint;

    private Paint borderPaint;
    private Path borderPath1;
    private Path borderPath2;
    private Path borderPath3;
    private Path borderPath4;
    private float borderEdgeSize;

    public CropOverlayView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_STROKE_WIDTH);
        borderPaint.setAntiAlias(true);
    }

    @Override
    public void createFrame(int width, int height) {
        float size = Math.min(width * 0.8f, height * 0.8f);
        float left = (width - size) / 2f;
        float right = size + left;
        float top = (height - size) / 2f;
        float bottom = top + size;

        mOuterFrame = new Frame(left, right, top, bottom);
        mInnerFrame = new Frame(left, right, top, bottom);

        borderEdgeSize = (int) mOuterFrame.getHeight() / 6;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mOuterFrame = null;
        mInnerFrame = null;
        mBitmap = null;

        borderPath1 = null;
        borderPath2 = null;
        borderPath3 = null;
        borderPath4 = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (mBitmap == null || mOuterFrame == null || mInnerFrame == null) {
            synchronized (canvas) {
                createFrame(width, height);
                mBitmap = createBitmap(width, height);
            }
        }

        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    private Bitmap createBitmap(int width, int height) {
        if (mOuterFrame == null || mInnerFrame == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw overlay layer
        mBitmapPaint.setColor(Color.BLACK);
        mBitmapPaint.setAlpha(125);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBitmapPaint);

        // Clear overlay layer for Frame
        mBitmapPaint.setColor(Color.TRANSPARENT);
        mBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        canvas.drawRect(mOuterFrame.getLeft(), mOuterFrame.getTop(), mOuterFrame.getRight(), mOuterFrame.getBottom(), mBitmapPaint);

        // Draw Frame Border
        drawFrameBorder(canvas, mOuterFrame, borderEdgeSize);

        return bitmap;
    }

    private void drawFrameBorder(Canvas canvas, Frame frame, float edgeSize) {
        if (borderPath1 == null) {
            borderPath1 = new Path();
            borderPath1.moveTo(frame.getLeft() + edgeSize - (BORDER_STROKE_WIDTH / 2), frame.getTop() - (BORDER_STROKE_WIDTH / 2));
            borderPath1.lineTo(frame.getLeft() - (BORDER_STROKE_WIDTH / 2), frame.getTop() - (BORDER_STROKE_WIDTH / 2));
            borderPath1.lineTo(frame.getLeft() - (BORDER_STROKE_WIDTH / 2), frame.getTop() + edgeSize - (BORDER_STROKE_WIDTH / 2));
        }
        if (borderPath2 == null) {
            borderPath2 = new Path();
            borderPath2.moveTo(frame.getLeft() + edgeSize - (BORDER_STROKE_WIDTH / 2), frame.getBottom() + (BORDER_STROKE_WIDTH / 2));
            borderPath2.lineTo(frame.getLeft() - (BORDER_STROKE_WIDTH / 2), frame.getBottom() + (BORDER_STROKE_WIDTH / 2));
            borderPath2.lineTo(frame.getLeft() - (BORDER_STROKE_WIDTH / 2), frame.getBottom() - edgeSize + (BORDER_STROKE_WIDTH / 2));
        }
        if (borderPath3 == null) {
            borderPath3 = new Path();
            borderPath3.moveTo(frame.getRight() - edgeSize + (BORDER_STROKE_WIDTH / 2), frame.getTop() - (BORDER_STROKE_WIDTH / 2));
            borderPath3.lineTo(frame.getRight() + (BORDER_STROKE_WIDTH / 2), frame.getTop() - (BORDER_STROKE_WIDTH / 2));
            borderPath3.lineTo(frame.getRight() + (BORDER_STROKE_WIDTH / 2), frame.getTop() + edgeSize - (BORDER_STROKE_WIDTH / 2));
        }
        if (borderPath4 == null) {
            borderPath4 = new Path();
            borderPath4.moveTo(frame.getRight() - edgeSize + (BORDER_STROKE_WIDTH / 2), frame.getBottom() + (BORDER_STROKE_WIDTH / 2));
            borderPath4.lineTo(frame.getRight() + (BORDER_STROKE_WIDTH / 2), frame.getBottom() + (BORDER_STROKE_WIDTH / 2));
            borderPath4.lineTo(frame.getRight() + (BORDER_STROKE_WIDTH / 2), frame.getBottom() - edgeSize + (BORDER_STROKE_WIDTH / 2));
        }
        canvas.drawPath(borderPath1, borderPaint);
        canvas.drawPath(borderPath2, borderPaint);
        canvas.drawPath(borderPath3, borderPaint);
        canvas.drawPath(borderPath4, borderPaint);
    }
}
