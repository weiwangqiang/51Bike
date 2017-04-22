package com.joshua.a51bike.activity.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.joshua.a51bike.Interface.MyAlerDialog;
import com.joshua.a51bike.R;

import java.io.File;

/**
 * 获取用户头像的对话框
 *
 * Created by wangqiang on 2017/1/9.
 */

public class GetIcnAlerDialog extends MyAlerDialog implements View.OnClickListener{
    String TAG ="MarginAlerDialog";
    String filePath;

    //拍照和图库的Intent请求码
    public final int TAKE_PHOTO_WITH_DATE = 200;
    public final int TAKE_PHOTO_FROM_IMAGE = 201;
    private Button camera;
    private Button pictrue;
    private Button cancel;


    private Context context;
    public GetIcnAlerDialog(Context context,String filePath) {
        super(context);
        this.context = context;
        this.filePath = filePath;

    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_geticn);
        getWindow().setBackgroundDrawable(new ColorDrawable());

        camera = (Button) findViewById(R.id.dialog_camera);
        pictrue = (Button)findViewById(R.id.dialog_picture);
         cancel = (Button) findViewById(R.id.dialog_cancel);

        cancel.setOnClickListener(this);
        camera.setOnClickListener(this);
        pictrue.setOnClickListener(this);

        setCanceledOnTouchOutside(true);
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setWindowAnimations(R.style.popWindow_animation);
    }
    @Override
    protected  void onStart(){
        super.onStart();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_cancel:
                Log.w(TAG,"----------->> dialog is cancel !!!");
                cancel();
                break;
            case R.id.dialog_camera:
                cancel();
                String SDState = Environment.getExternalStorageState();
                if (SDState.equals(Environment.MEDIA_MOUNTED)) {
                    File localFile = new File( Environment.getExternalStorageDirectory()+"/51get/");
                    if (localFile.exists()) {
                        Log.i(TAG, "onClick: "+" 存在该路径 file : "+localFile);
                        localFile.delete();
                    }
                    localFile.mkdir();
                    Log.i(TAG,"-->进入拍照 存放 ："+filePath);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri imageUri = Uri.fromFile(new File(filePath));
                    //设置输出路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    //设置输出格式
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    //是否以bitmap 形式返回照片
                    intent.putExtra("return-data", false);
                    ((Activity)context).startActivityForResult(intent, TAKE_PHOTO_WITH_DATE);

                }
                break;
            case R.id.dialog_picture:
                Intent intent ;
                if (Build.VERSION.SDK_INT >= 19 ) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }else
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("return_data", true);
                ((Activity)context).startActivityForResult(intent, TAKE_PHOTO_FROM_IMAGE);
                cancel();
                break;

        }
    }

    @Override
    public void myShow() {
        show();
    }

    @Override
    public void myCancel() {
        cancel();
    }
}
