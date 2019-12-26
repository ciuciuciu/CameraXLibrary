package com.ciuciu.camerax.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class ImageLoader {

    public static void loadImage(ImageView imageView, File imageFile) {
        Glide.with(imageView)
                .load(imageFile)
                .into(imageView);
    }

    public static void loadCircleImage(ImageView imageView, File imageFile) {
        Glide.with(imageView)
                .load(imageFile)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }
}
