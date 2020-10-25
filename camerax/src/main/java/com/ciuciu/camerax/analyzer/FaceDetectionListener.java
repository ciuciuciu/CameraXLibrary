package com.ciuciu.camerax.analyzer;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.util.List;

public interface FaceDetectionListener {

    void onFaceDetectionSuccess(FirebaseVisionImage firebaseVisionImage, List<FirebaseVisionFace> faces);

    void onFaceDetectionFailed();
}
