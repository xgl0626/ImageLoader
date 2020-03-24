package com.example.imageloader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import java.util.logging.LogRecord;
//**
// 2020.2.22
// 图片加载库
// 徐国林
// 实现功能：同步，异步加载，停止时加载图片，三级缓存 lrucache,disklrucache,http网络加载，md5文件加密，
// bitmap压缩：resources，filedirector
// tools是imageloader的一些工具
// */
public class ImageLoader {
    final static String TAG = "imageloader";
    final static String LOAD_RESULT = "1";
    final static int IMAGE_TAG = R.id.image_photo;
    private static ImageLoader mInstance;
    MemoryCahce memoryCahce = new MemoryCahce();
    NativeLruCahce nativeLruCahce = new NativeLruCahce();

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        public void hanlderMessage(Message msg) {
            ImageVIewBean imageVIewBean = (ImageVIewBean) msg.obj;
            ImageView imageView = imageVIewBean.imageView;
//            imageView.setImageBitmap(imageVIewBean.bitmap);
            String uri = (String) imageView.getTag(IMAGE_TAG);
            if (uri.equals(imageVIewBean.uri)) {
                imageView.setImageBitmap(imageVIewBean.bitmap);
            } else
                Log.w(TAG, "set image bitmap,but uri has change,ignore");
        }
    };

    //懒加载单例对象
    public static ImageLoader getInstance() {

        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader();
                }
            }
        }
        return mInstance;
    }

    //同步加载
    public Bitmap loaderImage(String imageUrl,int reqWidth,int reqHeight) {
        String key = new Tools().hashKeyForDisk(imageUrl);
        Bitmap bitmap = memoryCahce.get(key);
        if (bitmap != null) {
            return bitmap;
        }
        try {
            bitmap = nativeLruCahce.get(key);
            if (bitmap != null) {
                return bitmap;
            }
            if (bitmap == null) {
                bitmap = loadImageFromHttp(imageUrl,reqWidth,reqHeight);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap loadImageFromHttp(final String uri, final int reqWidth, final int reqHeight) {
        Request request = new Request.Builder().setAddress(uri).build();
        try {
            HttpConnections.sendRequestWithHttpURLConnection(request, new Callback() {
                @Override
                public Bitmap onResponse(HttpConnections.Response response) {
                    if (response != null) {
                        String key = new Tools().hashKeyForDisk(uri);
                        try {
                            nativeLruCahce.add(key, Tools.toBitmap(response.getmData()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        图片压缩
                        Bitmap bitmap = nativeLruCahce.getCutBitmap(uri,reqWidth,reqHeight);
                        if (bitmap != null) {
                            memoryCahce.add(key, bitmap);
                        }
                        return bitmap;
                    }
                    return null;
                }

                @Override
                public void onFail(Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //异步加载
    public void BindBitmap(String uri, ImageView imageView) {
        BindBitmap(uri, imageView, 0, 0);
    }

    public void BindBitmap(final String uri, final ImageView imageView, final int reqHeight, final int reqWidth) {
        String key = new Tools().hashKeyForDisk(uri);
        imageView.setTag(IMAGE_TAG, uri);
        final Bitmap bitmap = memoryCahce.get(key);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap1 = loaderImage(uri,reqWidth,reqHeight);
                if (bitmap1 != null) {
                    ImageVIewBean imageVIewBean = new ImageVIewBean(imageView, uri, bitmap1);
                    mMainHandler.obtainMessage(Integer.parseInt(LOAD_RESULT), imageVIewBean)
                            .sendToTarget();
                }
            }
        };
        SingleThreadManger.getInstance().getService().execute(loadBitmapTask);
    }

    private static class ImageVIewBean {
        public ImageView imageView;
        public String uri;
        public Bitmap bitmap;

        public ImageVIewBean(ImageView imageView, String uri, Bitmap bitmap) {
            this.imageView = imageView;
            this.bitmap = bitmap;
            this.uri = uri;
        }
    }
}
