package com.ciuciu.cameralibrary.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FileUtils {

    public static final String FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS";
    public static final String PHOTO_EXTENSION = ".jpg";

    public static File createFile(File baseFolder, String format, String extension) {
        return new File(baseFolder,
                new SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension);
    }
}
