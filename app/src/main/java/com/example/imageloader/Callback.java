package com.example.imageloader;

import android.graphics.Bitmap;

public interface Callback {

        Bitmap onResponse(HttpConnections.Response response);
        void onFail(Exception e);

}
