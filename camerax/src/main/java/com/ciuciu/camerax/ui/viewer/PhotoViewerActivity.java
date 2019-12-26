package com.ciuciu.camerax.ui.viewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ciuciu.camerax.R;

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
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);
        }
    }
}
