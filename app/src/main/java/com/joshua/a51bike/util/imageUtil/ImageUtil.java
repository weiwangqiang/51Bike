package com.joshua.a51bike.util.imageUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wangqiang on 2017/3/22.
 * image处理util
 */

public class ImageUtil {
    private static String TAG = "ImageUtil";

    /**
     * 压缩图片
     * @param picturePath 要压缩的图片路径
     * @param after_image_path 压缩后的图片路径
     * @return 返回压缩的路径
     */
    public static String compress(String  picturePath,String after_image_path){
        Log.i(TAG, "compress: --------------------------------------------------");
        Log.i(TAG, "compress: file "+picturePath);

        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(picturePath,options);
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        /**
         * 这样获取的inSampleSize 有问题
         */
//        options.inSampleSize = calculateInSampleSize(picturePath);
        long  blockSize = getFileSize(new File(picturePath));
        Log.i(TAG, "compress: befor size : "+blockSize/1024+" k ");
        if((blockSize / 1024) <= 1024){
            options.inSampleSize = 2;
        }else
            options.inSampleSize = 4;

        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;

        return saveImage(BitmapFactory.decodeFile(picturePath,options),after_image_path);
    }
    /** 有问题， 保存的图片变大了
     * b保存压缩后的图片
     * @param bitmap
     */
    private static String saveImage(Bitmap bitmap,String savefile) {
        try {
            FileOutputStream out = new FileOutputStream(savefile);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                bitmap.recycle();
                long  blockSize = getFileSize(new File(savefile));
                Log.i(TAG, "saveImage: blockSize is "+blockSize/1024+" k ");

                return savefile;
            }else{
                bitmap.recycle();
                out.flush();
                out.close();
            }
            return "";
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    return "";
    }

    /**
     * 计算inSampleSize 的大小
     * @param savefile
     * @return
     */
    public static int calculateInSampleSize(String savefile ) {
        int inSampleSize = 1;
        long  blockSize = getFileSize(new File(savefile));
        Log.i(TAG, "calculateInSampleSize: image size is "+blockSize /1024 +" k ");
        if((blockSize /1024) <= 1024){
            Log.i(TAG, "calculateInSampleSize: inSampleSize is "+inSampleSize);
            return inSampleSize;
        }else {
            /**
             * 根据 file 获取指定文件大小
             *
             * @param
             * @return
             * @throws Exception
             */
            inSampleSize = (int)(blockSize /( 1024 * 1024));
            Log.i(TAG, "calculateInSampleSize: inSampleSize is "+inSampleSize);
            return inSampleSize;
        }
    }
    private static long getFileSize(File file)  {
        Log.i(TAG, "getFileSize: file " +file );
        long size = 0;
        try{
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();

            } else {
//                file.createNewFile();
                Log.e(TAG , "文件不存在!");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return size;
    }



}
