package com.ciuciu.camerax.analyzer;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.ciuciu.camerax.controller.overlay.BaseOverlayView;
import com.ciuciu.camerax.controller.overlay.Frame;
import com.ciuciu.camerax.utils.BitmapUtils;
import com.ciuciu.camerax.utils.FrameMetadata;
import com.ciuciu.camerax.utils.ImageUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MLKitFacesAnalyzer implements ImageAnalysis.Analyzer {
    private static String TAG = "MLKitFacesAnalyzer";

    private BaseOverlayView mOverlayView;
    private FaceDetectionListener mCallback;

    private FirebaseVisionFaceDetector faceDetector;
    private FirebaseVisionImage firebaseVisionImage;
    private Bitmap croppedBmp;

    public MLKitFacesAnalyzer(BaseOverlayView overlayView, FaceDetectionListener callback) {
        this.mOverlayView = overlayView;
        this.mCallback = callback;

        FirebaseVisionFaceDetectorOptions detectorOptions = new FirebaseVisionFaceDetectorOptions.Builder()
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build();
        faceDetector = FirebaseVision
                .getInstance()
                .getVisionFaceDetector(detectorOptions);
    }

    @Override
    public void analyze(ImageProxy imageProxy, int rotationDegrees) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            return;
        }

        Log.i(TAG, "=============================================");
        Log.i(TAG, "start analyze ");

        analyzeWithoutCrop(imageProxy, rotationDegrees);

        //analyzeWithCropYUVFormat(imageProxy, rotationDegrees);

        //analyzeWithCropRGBFormat(imageProxy, rotationDegrees);
    }

    OnSuccessListener mOnFaceDetectSuccess = new OnSuccessListener<List<FirebaseVisionFace>>() {

        @Override
        public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
            if (mCallback != null) {
                mCallback.onFaceDetectionSuccess(firebaseVisionImage, firebaseVisionFaces);
            }
        }
    };

    OnFailureListener mOnFaceDetectFailed = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception ex) {
            ex.printStackTrace();
            if (mCallback != null) {
                mCallback.onFaceDetectionFailed();
            }
        }
    };

    private void analyzeWithoutCrop(ImageProxy imageProxy, int rotationDegrees) {
        Image image = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(rotationDegrees);
        firebaseVisionImage = FirebaseVisionImage.fromMediaImage(image, rotation);

        // Detect Faces
        faceDetector
                .detectInImage(firebaseVisionImage)
                .addOnSuccessListener(mOnFaceDetectSuccess)
                .addOnFailureListener(mOnFaceDetectFailed);
    }

    private void analyzeWithCropYUVFormat(ImageProxy imageProxy, int rotationDegrees) {
        try {
            Image image = imageProxy.getImage();

            Rect cropRect = getCropRectAccordingToRotation(image, rotationDegrees);
            image.setCropRect(cropRect);

            byte[] byteArray = ImageUtil.YUV420toNV21(image);
            Bitmap cropBitmap = BitmapUtils.getBitmap(byteArray, new FrameMetadata(cropRect.width(), cropRect.height(), rotationDegrees));

            // Detect Faces
            faceDetector
                    .detectInImage(FirebaseVisionImage.fromBitmap(cropBitmap))
                    .addOnSuccessListener(mOnFaceDetectSuccess)
                    .addOnFailureListener(mOnFaceDetectFailed);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void analyzeWithCropRGBFormat(ImageProxy imageProxy, int rotationDegrees) {
        Image image = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(rotationDegrees);
        firebaseVisionImage = FirebaseVisionImage.fromMediaImage(image, rotation);
        // Crop Bitmap here
        try {
            Frame transformFrame = mOverlayView.getOutputTransformFrame(new Size(image.getWidth(), image.getHeight()));
            if (transformFrame != null && transformFrame.toRect() != null) {
                image.setCropRect(transformFrame.toRect());

                if (croppedBmp != null) {
                    croppedBmp.recycle();
                }
                croppedBmp = Bitmap.createBitmap(firebaseVisionImage.getBitmap(),
                        (int) transformFrame.getLeft(),
                        (int) transformFrame.getTop(),
                        (int) transformFrame.getWidth(),
                        (int) transformFrame.getHeight());

                // Detect Faces
                faceDetector
                        .detectInImage(FirebaseVisionImage.fromBitmap(croppedBmp))
                        .addOnSuccessListener(mOnFaceDetectSuccess)
                        .addOnFailureListener(mOnFaceDetectFailed);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Rect getCropRectAccordingToRotation(Image image, int rotation) {
        int startX;
        int numberPixelW;
        int startY;
        int numberPixelH;

        switch (rotation) {
            case 0:
                startX = (int) (mOverlayView.getRelativePosX() * image.getWidth());
                numberPixelW = (int) (mOverlayView.getRelativePosWidth() * image.getWidth());
                startY = (int) (mOverlayView.getRelativePosY() * image.getHeight());
                numberPixelH = (int) (mOverlayView.getRelativePosHeight() * image.getHeight());
                return new Rect(startX, startY, startX + numberPixelW, startY + numberPixelH);

            case 90:
                startX = (int) (mOverlayView.getRelativePosY() * image.getWidth());
                numberPixelW = (int) (mOverlayView.getRelativePosHeight() * image.getWidth());
                numberPixelH = (int) (mOverlayView.getRelativePosWidth() * image.getHeight());
                startY = (int) (image.getHeight() - (mOverlayView.getRelativePosX() * image.getHeight()) - numberPixelH);
                return new Rect(startX, startY, startX + numberPixelW, startY + numberPixelH);

            case 180:
                numberPixelW = (int) (mOverlayView.getRelativePosWidth() * image.getWidth());
                startX = (int) (image.getWidth() - mOverlayView.getRelativePosX() * image.getWidth() - numberPixelW);
                numberPixelH = (int) (mOverlayView.getRelativePosHeight() * image.getHeight());
                startY = (int) (image.getHeight() - mOverlayView.getRelativePosY() * image.getHeight() - numberPixelH);
                return new Rect(startX, startY, startX + numberPixelW, startY + numberPixelH);

            case 270:
                numberPixelW = (int) (mOverlayView.getRelativePosHeight() * image.getWidth());
                numberPixelH = (int) (mOverlayView.getRelativePosWidth() * image.getHeight());
                startX = (int) (image.getWidth() - mOverlayView.getRelativePosY() * image.getWidth() - numberPixelW);
                startY = (int) (mOverlayView.getRelativePosX() * image.getHeight());
                return new Rect(startX, startY, startX + numberPixelW, startY + numberPixelH);

            default:
                throw new IllegalArgumentException("Rotation degree ($rotation) not supported!");
        }
    }

    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException("Rotation must be 0, 90, 180, or 270.");
        }
    }
}
