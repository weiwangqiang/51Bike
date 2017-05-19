package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.GetIcnAlerDialog;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.customview.CircleImageView;
import com.joshua.a51bike.customview.MyButton;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.ToastUtil;
import com.joshua.a51bike.util.imageUtil.ImageLFMemory;
import com.joshua.a51bike.util.imageUtil.ImageManager;
import com.joshua.a51bike.util.imageUtil.ImageUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * class description here
 *
 * 个人信息界面
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */

@ContentView(R.layout.user_infor)
public class UserInfor extends BaseActivity  {
    private String TAG = "UserInfor";
    //信息上传路径
    private String MesUrl = AppUtil.BaseUrl + "/user/updateUser";
    //图片上传路径
    private String PicUrl = AppUtil.BaseUrl+"/user/fileUpload";
    String pre_image_path = Environment.getExternalStorageDirectory()+"/51get";
    String after_image_path ;
    //最终上传图片的file
    File file_post = null ;
    //拍照和图库的Intent请求码
    public final int TAKE_PHOTO_WITH_DATE = 200;
    public final int TAKE_PHOTO_FROM_IMAGE = 201;
    private MyButton userName,renzhen,userPhone,mySchool;
    private Toolbar myToolbar;
    private TextView TextViewName;

    @ViewInject(R.id.user_infor_icn)
    private CircleImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(userControl.getUser()!=null)
            after_image_path
                    = pre_image_path +"/"+userControl.getUser().getUsername()+".jpg";
        init();
        ImageManager manager = new  ImageManager();
        if(userControl.getUser().getUserpic() != null){
            manager.bindImageWithBitmap(imageView,
                    after_image_path);
        }
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
        mySchool = (MyButton) findViewById(R.id.user_infor_school);
        TextViewName = (TextView) findViewById(R.id.main_user_name);
    }

    private void initActionBar() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    public void setLister() {
//        findViewById(R.id.user_infor_Logout).setOnClickListener(this);
        findViewById(R.id.user_infor_icn).setOnClickListener(this);
        findViewById(R.id.user_infor_school).setOnClickListener(this);
//        findViewById(R.id.user_infor_save).setOnClickListener(this);
        userPhone.setOnClickListener(this);
        renzhen.setOnClickListener(this);
    }
    /**
     * 初始化用户信息
     */
    private void initUserMessage() {
        if(userControl.getUser().getRealName() != null){
            renzhen.setClickable(false);
            renzhen.setRightText("已认证");
            userName.setRightText(userControl.getUser().getRealName());
        }
        else{
            renzhen.setClickable(true);
            renzhen.setRightText("未认证");
            userName.setRightText("无");
        }
        if(userControl.getUser().getSchool() != ""){
            mySchool.setRightText("已认证");
            mySchool.setClickable(false);

        }
        else{
            mySchool.setClickable(true);
            mySchool.setRightText("未认证");

        }

        userPhone.setRightText(userControl.getUser().getUsername());
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
            case R.id.user_infor_icn:
                changeIcn();
                break;
            case R.id.user_infor_phone:
                userControl.userInforPhoneBefor(UserInfor.this);
                break;
            case R.id.user_inf_renzhen:
                startActivity(new Intent(this,RZRealName.class));
                break;
            case R.id.user_infor_school:
                startActivity(new Intent(this,RZSchool.class));
                break;
            default:
                break;
        }
    }
    public void post(){
        if(file_post == null){
            ToastUtil.show(UserInfor.this,"请选择文件");
            return ;
        }
        dialogControl.setDialog(new WaitProgress(UserInfor.this));
        dialogControl.show();

        Log.i(TAG, "post: url : "+PicUrl);
        Log.i(TAG, "post: file : "+file_post);
        RequestParams params = new RequestParams(PicUrl);
        // 加到url里的参数, http://xxxx/s?wd=xUtils
        params.addQueryStringParameter("userid", userControl.getUser().getUserid()+"");
        // 添加到请求body体的参数, 只有POST, PUT, PATCH, DELETE请求支持.
        // params.addBodyParameter("wd", "xUtils");

        // 使用multipart表单上传文件
        params.setMultipart(true);
//        params.addBodyParameter(
//                "file",
//                new File(filePath),
//                null); // 如果文件没有扩展名, 最好设置contentType参数.
        try {
            params.addBodyParameter(
                    "file",
                    new FileInputStream(file_post),
                    "image/jpeg",
                    // 测试中文文件名
                    userControl.getUser().getUsername()+".jpg"); // InputStream参数获取不到文件名, 最好设置, 除非服务端不关心这个参数.
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                Log.i(TAG, "onSuccess: "+result);
                userControl.getUser().setUserpic(result);
                dialogControl.cancel();

                saveIcnToLocal();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                dialogControl.cancel();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {
                dialogControl.cancel();

            }
        });
    }
    /**
     * 更改头像
     */
    private void changeIcn() {
//        userControl.getUserIcn(UserInfor.this,filePath);
        dialogControl.setDialog(new GetIcnAlerDialog(this ,after_image_path));
        dialogControl.show();
    }
    public void upUserIcn(){
        if(file_post == null) return;
        imageView.setImageDrawable(Drawable.createFromPath(after_image_path));
    }
    public void saveIcnToLocal(){
        ImageLFMemory imageLFMemory = ImageLFMemory.InStance();
        imageLFMemory.setBitmapToMemory(after_image_path, BitmapFactory.decodeFile(after_image_path));
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
     * 获取拍照的图片
     */
    private void getphoto(){
        //由file获取拍照的图片
//        Uri uri = Uri.fromFile(new File(filePath));
        //压缩获得的照片
        after_image_path =  ImageUtil.compress(after_image_path,after_image_path);
        //更新当前file路径
        file_post = new File(after_image_path);
        //更新ui界面
        upUserIcn();
        saveIcnToLocal();
        Log.i(TAG, "getphoto: file  "+file_post);
    }
    /**
     * 获取图库中的图片
     * @param data
     */
    private void pickphoto(Intent data) {
        if (data == null) return;
        file_post = UriToFile(data.getData());
        upUserIcn();
        saveIcnToLocal();

        Log.i(TAG,"-->获取图库图片 file is :\n "+file_post);
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
                Cursor cursor = UserInfor.this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
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
}