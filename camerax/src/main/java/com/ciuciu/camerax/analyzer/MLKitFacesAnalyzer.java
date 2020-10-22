package com.ciuciu.camerax.analyzer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.ciuciu.camerax.controller.overlay.BaseOverlayView;
import com.ciuciu.camerax.controller.overlay.Frame;
import com.ciuciu.camerax.utils.BitmapUtils;
import com.ciuciu.camerax.utils.ImageUtil;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MLKitFacesAnalyzer implements ImageAnalysis.Analyzer {
    private static String TAG = "MLKitFacesAnalyzer";

    private FirebaseVisionFaceDetector faceDetector;
    private FirebaseVisionImage fbImage;

    Activity activity;
    ImageView preview;
    BaseOverlayView mCropFrame;

    public MLKitFacesAnalyzer(Activity activity, ImageView preview, BaseOverlayView cropFrame) {
        this.activity = activity;
        this.preview = preview;
        this.mCropFrame = cropFrame;
    }

    @Override
    public void analyze(ImageProxy image, int rotationDegrees) {
        if (image == null || image.getImage() == null) {
            return;
        }

        int rotation = degreesToFirebaseRotation(rotationDegrees);

        Frame frame = mCropFrame.getOutputTransformFrame(image, rotation);
        //Frame frame = mCropFrame.getOutputTransformFrame(new Size(image.getWidth(), image.getHeight()));
        if (frame != null && frame.toRect() != null) {
            try {
                Rect cropRect = frame.toRect();
                image.setCropRect(cropRect);
                byte[] bytes = ImageUtil.imageToJpegByteArray(image);

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Log.d(TAG, "width " + bitmap.getWidth() + " : " + bitmap.getHeight());
                            preview.setImageBitmap(bitmap);
                        } catch (Exception ex) {

                        }

                    }
                });
                //fbImage = FirebaseVisionImage.fromMediaImage(image.getImage(), rotation);
                fbImage = FirebaseVisionImage.fromBitmap(bitmap);

                initDetector();
                detectFaces();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Log.d(TAG, "width " + image.getImage().getWidth());


        //Log.d(TAG, "---------------------------------------------------------------------------------------- ");
    }

    private void initDetector() {
        FirebaseVisionFaceDetectorOptions detectorOptions = new FirebaseVisionFaceDetectorOptions
                .Builder()
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build();
        faceDetector = FirebaseVision
                .getInstance()
                .getVisionFaceDetector(detectorOptions);
    }

    private void detectFaces() {
        faceDetector
                .detectInImage(fbImage)
                .addOnSuccessListener(firebaseVisionFaces -> {
                    if (!firebaseVisionFaces.isEmpty()) {
                        processFaces(firebaseVisionFaces);
                    }
                }).addOnFailureListener(e -> Log.i(TAG, e.toString()));
    }

    private void processFaces(List<FirebaseVisionFace> faces) {
        Log.i(TAG, "processFaces " + faces.size());
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
