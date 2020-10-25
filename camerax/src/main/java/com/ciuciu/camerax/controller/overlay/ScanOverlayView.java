package com.ciuciu.camerax.controller.overlay;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

public class ScanOverlayView extends BaseOverlayView {

    private final float BORDER_STROKE_WIDTH = 12;
    private final float SCAN_LINE_HEIGHT = 4;
    private final float SCAN_LINE_GRADIENT_HEIGHT = 48;

    private final int GRADIENT_COLOR_1 = Color.WHITE;
    private final int GRADIENT_COLOR_2 = Color.TRANSPARENT;

    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Paint mClearDuffPaint;

    // Border
    private Paint borderPaint;
    private Path borderPath1;
    private Path borderPath2;
    private Path borderPath3;
    private Path borderPath4;
    private float borderEdgeSize;

    // Scan Line
    private Bitmap mScanLineBitmap;
    private Paint scanLinePaint;
    private Paint scanGradientPaint;
    private ValueAnimator mAnimator;
    private boolean scanLineRunning = true;
    private boolean scanLineDown = true;
    private float scanLinePositionY;


    public ScanOverlayView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Border
        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_STROKE_WIDTH);
        borderPaint.setAntiAlias(true);

        mClearDuffPaint = new Paint();
        mClearDuffPaint.setColor(Color.TRANSPARENT);
        mClearDuffPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // Scan Line
        scanLinePaint = new Paint();
        scanLinePaint.setColor(Color.WHITE);
        scanLinePaint.setStyle(Paint.Style.STROKE);
        scanLinePaint.setStrokeWidth(SCAN_LINE_HEIGHT);
        scanLinePaint.setAntiAlias(true);

        scanGradientPaint = new Paint();
        scanGradientPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        scanGradientPaint.setStrokeWidth(SCAN_LINE_GRADIENT_HEIGHT);
        scanGradientPaint.setAntiAlias(true);

        mAnimator = ValueAnimator.ofFloat(0f, 100f);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (mOuterFrame != null) {

                    if (scanLineDown) {
                        scanLinePositionY += mOuterFrame.getHeight() / 100f;
                        if (scanLinePositionY > mOuterFrame.getBottom() + SCAN_LINE_HEIGHT + SCAN_LINE_GRADIENT_HEIGHT) {
                            scanLineDown = false;
                        }
                    } else {
                        scanLinePositionY -= mOuterFrame.getHeight() / 100f;
                        if (scanLinePositionY < mOuterFrame.getTop() - SCAN_LINE_HEIGHT - SCAN_LINE_GRADIENT_HEIGHT) {
                            scanLineDown = true;
                        }
                    }
                    invalidate();
                }
            }
        });
        mAnimator.start();
    }

    @Override
    public void createFrame(int width, int height) {

        mOuterFrame = new Frame.Builder()
                .widthSize(width, height)
                .fromRelative(0.15f, 0.2f, 0.7f, 0.6f)
                .build();

        mInnerFrame = new Frame.Builder()
                .widthSize(width, height)
                .fromRelative(0.15f, 0.2f, 0.7f, 0.6f)
                .build();

        borderEdgeSize = (int) mOuterFrame.getHeight() / 6;
        scanLinePositionY = mOuterFrame.getTop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mOuterFrame = null;
        mInnerFrame = null;
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

        // Draw Scan Line
        if (scanLineRunning) {
            mScanLineBitmap = createScanLineBitmap(width, height, mOuterFrame);
            if (mScanLineBitmap != null) {
                canvas.drawBitmap(mScanLineBitmap, 0, 0, null);
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
        canvas.drawRect(mOuterFrame.getLeft(), mOuterFrame.getTop(), mOuterFrame.getRight(), mOuterFrame.getBottom(), mClearDuffPaint);

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

    private Bitmap createScanLineBitmap(int width, int height, Frame frame) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw Scan-Line
        scanLinePaint.setColor(Color.WHITE);
        canvas.drawLine(frame.getLeft(), scanLinePositionY, frame.getRight(), scanLinePositionY, scanLinePaint);

        float left = frame.getLeft() + (SCAN_LINE_GRADIENT_HEIGHT / 2f);
        float right = frame.getRight() - (SCAN_LINE_GRADIENT_HEIGHT / 2f);
        float top;
        float bottom;
        if (scanLineDown) {
            top = scanLinePositionY - (SCAN_LINE_HEIGHT / 2) - (SCAN_LINE_GRADIENT_HEIGHT / 2);
            bottom = top - SCAN_LINE_GRADIENT_HEIGHT - (SCAN_LINE_GRADIENT_HEIGHT / 2);
        } else {
            top = scanLinePositionY + (SCAN_LINE_HEIGHT / 2) + (SCAN_LINE_GRADIENT_HEIGHT / 2);
            bottom = top + SCAN_LINE_GRADIENT_HEIGHT + (SCAN_LINE_GRADIENT_HEIGHT / 2);
        }

        // Gradient-Effect
        scanGradientPaint.setShader(createGradientEffect(left, top, left, bottom));
        canvas.drawRect(left, top, right, bottom, scanGradientPaint);

        // Clear
        canvas.drawRect(0, 0, width, mOuterFrame.getTop(), mClearDuffPaint);
        canvas.drawRect(0, mOuterFrame.getBottom(), width, height, mClearDuffPaint);

        return bitmap;
    }

    private LinearGradient createGradientEffect(float left, float top, float right, float bottom) {
        return new LinearGradient(left, top, right, bottom,
                GRADIENT_COLOR_1, GRADIENT_COLOR_2, Shader.TileMode.CLAMP);
    }

}
