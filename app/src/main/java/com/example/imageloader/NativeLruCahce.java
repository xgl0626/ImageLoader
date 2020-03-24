package com.example.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.imageloader.Tools.getAppVersion;
import static com.example.imageloader.Tools.getDiskCacheDir;

public class NativeLruCahce implements ImageCahce{
    private DiskLruCache diskLruCache = null;
    public void caeateDlcahce() throws IOException {
        File cacheDir = getDiskCacheDir(MyApplication.getContext(), "bitmap");
        if(!cacheDir.exists())
        {
            cacheDir.mkdirs();
        }
        diskLruCache=DiskLruCache.open(cacheDir,getAppVersion(MyApplication.getContext()),1,5*1024*1024);
    }
    public void add(final String key, final Bitmap bitmap) throws IOException {
        SingleThreadManger.getInstance().getService().execute(new Runnable() {
            @Override
            public void run() {
                DiskLruCache.Editor editor = null;
                try {
                    editor = diskLruCache.edit(key);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (editor != null) {
                    try {
                        final OutputStream outputStream = editor.newOutputStream(0);
                        if (bitmap != null) {
                            outputStream.write(Tools.toBytes(bitmap));
                            editor.commit();
                        } else
                            editor.abort();
                        diskLruCache.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public Bitmap get(String key) {
        DiskLruCache.Snapshot snapShot = null;
        try {
            snapShot = diskLruCache.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (snapShot != null) {
            InputStream is = snapShot.getInputStream(0);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        }
        return null;
    }
    public Bitmap getCutBitmap(String imageUrl,int reqWidth,int reqHeight)
    {
        Bitmap bitmap=null;
        String key=new Tools().hashKeyForDisk(imageUrl);
        DiskLruCache.Snapshot snapshot=null;
        try {
            snapshot=diskLruCache.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(snapshot!=null)
        {
            FileInputStream fileInputStream=(FileInputStream)snapshot.getInputStream(0);
            try {
                FileDescriptor fileDescriptor=fileInputStream.getFD();
                bitmap=BitmapCutTool.decodeSampledBitmapFromFileDescriptor(fileDescriptor,reqWidth,reqHeight);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public void remove(String key) throws IOException {
        diskLruCache.remove(key);
    }
}


