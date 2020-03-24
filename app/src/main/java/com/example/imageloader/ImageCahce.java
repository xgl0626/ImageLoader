package com.example.imageloader;

import android.graphics.Bitmap;

import java.io.IOException;

public interface ImageCahce {
    Bitmap get(String uri);
    void add(String uri, Bitmap bitmap) throws IOException;
    void remove(String uri) throws IOException;
}
