package com.joshua.a51bike.util.imageUtil;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * class description here
 *  从内存中获取bitmap
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-23
 */
public class ImageLFMemory {
     private String    TAG = "ImageLFMmemory";
    private LruCache<String,Bitmap> mMemoryCache ;
    private static ImageLFMemory imageLFMemory = new ImageLFMemory();
    private ImageLFMemory() {
        long maxMemory = Runtime.getRuntime().maxMemory() / 8;// 模拟器默认是16M
        mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getRowBytes() * value.getHeight();// 获取图片占用内存大小
                return byteCount;
            }
        };
    }
    public static  ImageLFMemory InStance(){
        return imageLFMemory;
    }
    /**
     * 获取图片bitmap
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapFromMemory(String url){
        // SoftReference<Bitmap> softReference = mMemoryCache.get(url);
        // if (softReference != null) {
        // Bitmap bitmap = softReference.get();
        // return bitmap;
        // }
        if(mMemoryCache.size()==0 )
            return null;
        Bitmap bitmap = mMemoryCache.get(url);
        if((bitmap != null )&&bitmap.isRecycled())
            return null;
        return bitmap;
    }

    /**
     * 写入内存
     * @param url
     * @param bitmap
     */
    public void setBitmapToMemory(String url, Bitmap bitmap){
        // SoftReference<Bitmap> softReference = new
        // SoftReference<Bitmap>(bitmap);
        // mMemoryCache.put(url, softReference);
            try{
                mMemoryCache.put(url, bitmap);
            }catch(Exception e){
                    e.printStackTrace();
                }
    }
}
