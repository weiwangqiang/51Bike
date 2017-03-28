package com.joshua.a51bike.util.imageUtil;

import android.graphics.Bitmap;
import android.util.Log;

import com.joshua.a51bike.R;
import com.joshua.a51bike.customview.CircleImageView;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-23
 */
public class ImageManager {
    private String TAG = "ImageManager";

    public ImageManager() {
    }
    public void bindImageWithBitmap(CircleImageView imageView, String url){
        Log.i(TAG, "bindImageWithBitmap: ");
        ImageLFMemory mmemory =  ImageLFMemory.InStance();
        Bitmap bitmap = mmemory.getBitmapFromMemory(url);
        if(bitmap != null ){
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "bindImageWithBitmap:   success load image from memory  ");
            return ;
        }
        Log.i(TAG, "bindImageWithBitmap: load from local");
        ImageLocalLoad imageLocalLoad = new ImageLocalLoad();
        bitmap  = imageLocalLoad.getBitmapFromLocal(url);
        if(bitmap != null ){
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "bindImageWithBitmap:   success load image from local ");
            return ;
        }
        Log.i(TAG, "bindImageWithBitmap: load from net ");
//        ImageLFNet net = new ImageLFNet();
//        net.getBitmapFromNet(imageView,url);
        imageView.setImageResource(R.drawable.default_icn);
    }
}
