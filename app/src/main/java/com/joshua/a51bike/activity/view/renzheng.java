package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;

import org.xutils.view.annotation.ContentView;

import java.io.File;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-13
 */
@ContentView(R.layout.renzheng)
public class renzheng extends BaseActivity {
    private String TAG = "renzheng" ;
    String filePath = Environment.getExternalStorageDirectory()+"/51get";

    //拍照和图库的Intent请求码
    public final int TAKE_PHOTO_WITH_DATE = 200;
    public final int TAKE_PHOTO_FROM_IMAGE = 201;
    private UserControl userControl ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        userControl = UserControl.getUserControl();
        findId();
        setLister();
    }

    public void findId() {

    }

    public void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);
        findViewById(R.id.getSrc).setOnClickListener(this);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
                break;
            case R.id.getSrc:
                userControl.getUserIcn(renzheng.this);
                break;
            default:
                break;
        }
    }
    /**
     * 获取系统拍照或图库的回调函数
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.i(TAG,"Result Coode is "+resultCode+" requestCode is "+requestCode);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            //在拍照中获取图片
            case TAKE_PHOTO_WITH_DATE:
                getphoto();
                break;
            //在图库中获取图片
            case TAKE_PHOTO_FROM_IMAGE:
                pickphoto(data);
                break;
        }
    }
    /**
     * 在拍照中获取图片
     */
    private void getphoto(){
        //由file获取拍照的图片
       Uri  uri = Uri.fromFile(new File(filePath));
        Log.i(TAG,"-->获取拍照图片 file is :\n "+filePath+ " translate uri is ：\n "+uri);

    }
    /**
     * 在图库中获取图片
     * @param data
     */
    private void pickphoto(Intent data) {
        if (data == null) {
            return;
        }
       Uri  uri = data.getData();
        Log.i(TAG,"-->获取拍照图片 file is :\n "+filePath+ " translate uri is ：\n "+uri);

    }
}