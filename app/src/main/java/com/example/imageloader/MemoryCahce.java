package com.example.imageloader;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryCahce implements ImageCahce {
     private LruCache<String,Bitmap> mMemoryCache;
     public MemoryCahce()
     {
         int maxMemory = (int) Runtime.getRuntime().maxMemory();
         int cacheSize = maxMemory / 8;
         mMemoryCache = new LruCache<String, Bitmap>(cacheSize)
         {
             protected int sizeOf(String key,Bitmap bitmap)
             {
                 return bitmap.getRowBytes()*bitmap.getHeight()/1024;
             }
         };
     }
    @Override
    public Bitmap get(String key) {
       Bitmap bitmap=mMemoryCache.get(key);
       return bitmap;
    }

    @Override
    public void add(String key,Bitmap bitmap) {
        if(mMemoryCache.get(key)==null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    @Override
    public void remove(String key) {
        mMemoryCache.remove(key);
    }
}
