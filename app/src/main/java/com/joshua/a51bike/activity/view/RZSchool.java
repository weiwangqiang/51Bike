package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.GetIcnAlerDialog;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.customview.SchoolProgress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.MyTools;
import com.joshua.a51bike.util.imageUtil.ImageUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

/**
 * class description here
 *
 * 用户 学校认证界面
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-13
 */
@ContentView(R.layout.rz_school)
public class RZSchool extends BaseActivity {
    private String TAG = "RZSchool" ;
    private User user;
    private String url = AppUtil.BaseUrl + "/user/updateUser";
    private EditText getName, getNumber;
    private final int GET_PRO = 100;
    private final int GET_PRO_RESULT = 110;
    String pre_image_path = Environment.getExternalStorageDirectory()+"/51get";
    //拍照和图库的Intent请求码
    public final int TAKE_PHOTO_WITH_DATE = 200;
    public final int TAKE_PHOTO_FROM_IMAGE = 201;
    String after_image_path ;
    private TextView textView1,textView2,textView3,textView4;

    @ViewInject(R.id.renzheng_school)
    private TextView getSchool;

    @ViewInject(R.id.school_progress_view)
    private SchoolProgress useProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProgressView();
    }
    private void setProgressView() {
        Log.i(TAG, "setProgressView: ");
        useProgress.setPoistion(4);
        textView1.setTextColor(getResources().getColor(R.color.baseColor));
        textView4.setTextColor(getResources().getColor(R.color.baseColor));

    }
    public void init() {
        userControl = UserControl.getUserControl();
        findId();
        setLister();
        initActionBar();
        after_image_path = pre_image_path +"/"+userControl.getUser().getUsername()+".jpg";

    }
    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    public void findId() {
        getName = (EditText) findViewById(R.id.renzheng_name);
        getNumber = (EditText) findViewById(R.id.renzheng_stuNumber);

        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView3 = (TextView) findViewById(R.id.text3);
        textView4 = (TextView) findViewById(R.id.text4);

//        getName.setText(userControl.getUser().getUsername());
//        getId.setText("22");
//        getSchool.setText("hahaha");
    }

    public void setLister() {
        findViewById(R.id.rz_realName_submit).setOnClickListener(this);
        findViewById(R.id.rz_school_upPic).setOnClickListener(this);
        findViewById(R.id.renzheng_school).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rz_realName_submit:
                update();
                break;
            case R.id.rz_school_upPic:
                changeIcn();
                break;
            case R.id.renzheng_school:
                getSchool();
            default:
                break;
        }
    }
    public void getSchool(){
        startActivityForResult(new Intent(this,ChoiceProvince.class),GET_PRO);
    }
    /**
     * 更改头像
     */
    private void changeIcn() {
        dialogControl.setDialog(new GetIcnAlerDialog(this ,after_image_path));
        dialogControl.show();
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
     * 获取系统拍照或图库的回调函数
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.i(TAG,"Result Coode is "+resultCode+" requestCode is "+requestCode);
        if(requestCode == GET_PRO && resultCode == GET_PRO_RESULT ) {
            getSchool.setText("所在学校："+data.getStringExtra("school"));
            return;
        }
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
     * 获取拍照的图片
     */
    private void getphoto(){
        //由file获取拍照的图片
//        Uri uri = Uri.fromFile(new File(filePath));
        //压缩获得的照片
        after_image_path =  ImageUtil.compress(after_image_path,after_image_path);
        //更新当前file路径
        //更新ui界面
    }
    /**
     * 获取图库中的图片
     * @param data
     */
    private void pickphoto(Intent data) {
        if (data == null) return;
    }

    /**
     * 将uri转换为String
     * @param uri
     * @return
     */
    private File UriToFile(Uri uri){
        Log.i(TAG, "UriToFile: uri is : "+ uri);
        String picturePath = null;
        if (Build.VERSION.SDK_INT >= 19 ) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = { MediaStore.Images.Media.DATA };
            String sel = MediaStore.Images.Media._ID +"=?";
            Cursor cursor = RZSchool.this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                    sel, new String[] { id }, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                picturePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }else{
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            if(cursor.moveToFirst()){
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                picturePath = cursor.getString(column_index);
            }
            cursor.close();
            if(picturePath == null){
                picturePath  = uri.getPath();
            }
        }
        after_image_path =  ImageUtil.compress(picturePath,after_image_path);
        return new File(after_image_path);
    }
    private void update(){
        if (MyTools.EditTextIsNull(getName)
                || MyTools.EditTextIsNull(getNumber)
                || (getSchool.getText().length() == 0)) {
            uiUtils.showToast("不能为空");
            return;
        }
        String name = getName.getText().toString();
        String id = getNumber.getText().toString();
        String school = getSchool.getText().toString();

        RequestParams params = new RequestParams(url);
        params.addBodyParameter("username",name);
//        params.addBodyParameter("userpass",id);
//        params.addBodyParameter("userstate",id);

        userControl.getUser().setRealName(name);
        userControl.getUser().setSchool(school);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
        post(params);

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
                    finish();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                uiUtils.showToast("修改失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
    }


}