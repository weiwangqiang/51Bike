package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.joshua.a51bike.Interface.DialogCallBack;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.LogoutState;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.CurrencyAlerDialog;
import com.joshua.a51bike.tools.MyButton;
import com.joshua.a51bike.util.AppUtil;

import org.xutils.view.annotation.ContentView;

import java.io.File;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */

@ContentView(R.layout.user_infor)
public class UserInfor extends BaseActivity  {
    private String TAG = "UserInfor";
    private String url = AppUtil.BaseUrl + "/user/updateUser";
    String filePath = Environment.getExternalStorageDirectory()+"/51get";
    //拍照和图库的Intent请求码
    public final int TAKE_PHOTO_WITH_DATE = 200;
    public final int TAKE_PHOTO_FROM_IMAGE = 201;
    private MyButton userName,renzhen,userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    @Override
    protected void onResume() {
        super.onResume();
        initUserMessage();
    }
    public void init() {
        initActionBar();
        findView();

        setLister();
    }

    private void findView() {
        userName = (MyButton) findViewById(R.id.user_inf_name);
        renzhen = (MyButton) findViewById(R.id.user_inf_renzhen);
        userPhone = (MyButton) findViewById(R.id.user_infor_phone);
    }

    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    public void setLister() {
        findViewById(R.id.user_infor_Logout).setOnClickListener(this);
        findViewById(R.id.user_infor_icn).setOnClickListener(this);
        renzhen.setOnClickListener(this);
        userPhone.setOnClickListener(this);
    }
    /**
     * 初始化用户信息
     */
    private void initUserMessage() {
        userName.setRightText(userControl.getUser().getUsername());
        renzhen.setRightText(userControl.getUser().getUsermoney()+"");
        userPhone.setRightText(userControl.getUser().getUsermoney()+"");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            case R.id.user_infor_Logout:
                logout();
                break;
            case R.id.user_infor_icn:
                changeIcn();
                break;
            case R.id.user_infor_phone:
                userControl.userInforPhoneBefor(UserInfor.this);
                break;
            case R.id.user_inf_renzhen:
               userControl.renZheng(UserInfor.this);
                break;
            default:
                break;
        }
    }

    /**
     * 更改头像
     */
    private void changeIcn() {
        userControl.getUserIcn(UserInfor.this);
    }

    /**
     * 退出
     */
    private void logout() {

        dialogControl.setDialog(new CurrencyAlerDialog(UserInfor.this,"温馨提示",
                "确定要离开我吗？",new callback()));
        dialogControl.show();

    }
    public class callback implements DialogCallBack {

        @Override
        public void sure() {
            userControl.setUserState(new LogoutState());
            userControl.setUser(null);
            uiUtils.showToast("注销成功！");
            finish();
        }

        @Override
        public void cancel() {

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
        Uri uri = Uri.fromFile(new File(filePath));
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