package com.ciuciu.camerax.analyzer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.ciuciu.camerax.controller.overlay.BaseOverlayView;
import com.ciuciu.camerax.controller.overlay.Frame;
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
    private FirebaseVisionImage firebaseVisionImage;

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
        firebaseVisionImage = FirebaseVisionImage.fromMediaImage(image.getImage(), rotation);

        // Crop Bitmap here
        try {
            Frame transformFrame = mCropFrame.getOutputTransformFrame(new Size(firebaseVisionImage.getBitmap().getWidth(), firebaseVisionImage.getBitmap().getHeight()));
            if (transformFrame != null && transformFrame.toRect() != null) {

                Bitmap croppedBmp = Bitmap.createBitmap(firebaseVisionImage.getBitmap(),
                        (int) transformFrame.getLeft(),
                        (int) transformFrame.getTop(),
                        (int) transformFrame.getWidth(),
                        (int) transformFrame.getHeight());

                //activity.runOnUiThread(() -> preview.setImageBitmap(croppedBmp));


                // Init Face-Detector
                FirebaseVisionFaceDetectorOptions detectorOptions = new FirebaseVisionFaceDetectorOptions.Builder()
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();
                faceDetector = FirebaseVision
                        .getInstance()
                        .getVisionFaceDetector(detectorOptions);
                // Detect Faces
                faceDetector
                        .detectInImage(FirebaseVisionImage.fromBitmap(croppedBmp))
                        .addOnSuccessListener(firebaseVisionFaces -> {
                            if (!firebaseVisionFaces.isEmpty()) {
                                processFaces(firebaseVisionFaces);
                            }
                        }).addOnFailureListener(e -> Log.i(TAG, e.toString()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
