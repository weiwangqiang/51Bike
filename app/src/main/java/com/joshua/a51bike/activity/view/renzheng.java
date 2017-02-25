package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.MyTools;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

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
    private User user;
    private String url = AppUtil.BaseUrl + "/user/updateUser";
    String filePath = Environment.getExternalStorageDirectory()+"/51get";
    private EditText getName, getId,getSchool;
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

        getName = (EditText) findViewById(R.id.name);
        getId = (EditText) findViewById(R.id.id);
        getSchool = (EditText) findViewById(R.id.school);
        getName.setText(userControl.getUser().getUsername());
        getId.setText("22");
        getSchool.setText("hahaha");
    }

    public void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);
        findViewById(R.id.getSrc).setOnClickListener(this);
        findViewById(R.id.upDate).setOnClickListener(this);

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
            case R.id.upDate:
                update();
                break;
            default:
                break;
        }
    }
    private void update(){
        if (MyTools.EditTextIsNull(getName)
                || MyTools.EditTextIsNull(getId)
                || MyTools.EditTextIsNull(getSchool)) {
            return;
        }
        String name = getName.getText().toString();
        String id = getId.getText().toString();
        String school = getSchool.getText().toString();

        RequestParams params = new RequestParams(url);
        params.addBodyParameter("username",name);
//        params.addBodyParameter("userpass",id);
        params.addBodyParameter("userstate",id);

        user = new User();
        user.setUsername(name);
        user.setUserpass(id);
        user.setUserstate(1);
        post(params);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
    }

    private void post(RequestParams params){
        Log.d(TAG, "post: post by xutils------>>");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "success: object is "+ result.toString());
                dialogControl.cancel();

                if(result.equals("ok")){
                    uiUtils.showToast("修改成功！");
                    userControl.setUser(user);
                    finish();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                uiUtils.showToast("修改失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
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