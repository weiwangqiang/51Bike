package com.joshua.a51bike.util.imageUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-23
 */
public class ImageLocalLoad {
    private String TAG = "ImageLocalLoad";
    private String rootUrl = Environment.getExternalStorageDirectory()+"/51get";
    private StringBuilder sb = new StringBuilder(rootUrl);
    public ImageLocalLoad() {
        File file  = new File(rootUrl);
        if (!file.exists()) {
            file.mkdir();
        }
    }
    public   Bitmap getBitmapFromLocal(String imageUrl){
        String name =   imageUrl.substring(imageUrl.lastIndexOf("/"));
        sb.append(name);
        Log.i(TAG, "getBitmapFromLocal: sb is "+sb.toString());
        try {
            File file = new File(sb.toString());
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(
                        file));
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean setBitmapToLocal(String imageUrl,Bitmap bitmap){
        String name =   imageUrl.substring(imageUrl.lastIndexOf("/"));
        String url = rootUrl + name;
        Log.i(TAG, "setBitmapToLocal: url "+url);
    try{
        File file = new File(url.toString());
        if(file.exists()){
          file.delete();
        }
        Log.i(TAG, "setBitmapToLocal: file"+file);
        // 将图片保存在本地
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        try{
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "setBitmapToLocal: save  success");
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
