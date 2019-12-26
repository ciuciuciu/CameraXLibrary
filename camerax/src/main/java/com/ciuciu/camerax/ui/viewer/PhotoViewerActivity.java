package com.ciuciu.camerax.ui.viewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.ciuciu.camerax.R;
import com.ciuciu.camerax.utils.ImageLoader;

import java.io.File;

public class PhotoViewerActivity extends Activity {
    private static final String INTENT_KEY_PHOTO_FILE = "PhotoFile";

    public static void startActivity(Context context, File photoFile) {
        Intent intent = new Intent(context, PhotoViewerActivity.class);
        intent.putExtra(INTENT_KEY_PHOTO_FILE, photoFile);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        Intent intent = getIntent();

        File photoFile = (File) intent.getSerializableExtra(INTENT_KEY_PHOTO_FILE);
        if (photoFile != null) {
            ImageLoader.loadImage(findViewById(R.id.imageView), photoFile);
        }
    }
}
