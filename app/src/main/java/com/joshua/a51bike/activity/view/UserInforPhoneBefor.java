package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.MyTools;
import com.joshua.a51bike.util.UiUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Timer;
import java.util.TimerTask;

/**
 * class description here
 *
 *  更改手机验证界面
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-12
 */
@ContentView(R.layout.user_infor_pb)
public class UserInforPhoneBefor extends BaseActivity {
    private static final String TAG = "UserInforPhoneBefor";
    private  String resultCode ;
    private int type = 0;
    private static final int GET_CODE = 1;
    private static final int NEXT = 2;
    @ViewInject(R.id.getCode)
    private TextView getCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        initActionBar();
        findId();
        setLister();
    }

    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }
    private EditText Ephone,Ecode;
    public void findId() {
        Ephone = (EditText) findViewById(R.id.bp_phone_number);
        Ecode = (EditText) findViewById(R.id.bp_code);
    }


    public void setLister() {
        findViewById(R.id.getCode).setOnClickListener(this);
    }
    public void next(View view){
        if (MyTools.EditTextIsNull(Ephone)) {
            UiUtils.showToast("手机号不能为空~");
            return;
        }
        if (MyTools.EditTextIsNull(Ecode)) {
            UiUtils.showToast("验证码不能为空~");
            return;
        }

        type = NEXT;
        String code = Ecode.getText().toString();
//        if(!code.equals(resultCode)){
//            UiUtils.showToast("验证码出错了~");
//            return;
//        }
        startActivity(new Intent(this,UserInforPhoneAfter.class));
        finish();
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getCode:
                getCode();
                break;
            default:
                break;
        }
    }
    private String mesUrl = AppUtil.BaseUrl +"/user/getCode";

    /*获取验证码*/
    private void getCode() {
        if (MyTools.EditTextIsNull(Ecode)) {
            UiUtils.showToast("验证码不能为空~");
            return;
        }
        type = GET_CODE;
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
        RequestParams params = new RequestParams(mesUrl);
        params.addBodyParameter("phoneNumber",Ephone.getText().toString());
        post(params);
    }

    private void post(RequestParams params){
        Log.i(TAG, "post: "+params.toString());
       x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dialogControl.cancel();
                if(result.length() == 6 ){
                    resultCode = result;
                    startTime();
                    uiUtils.showToast("已发送验证码，请注意查收");
                }else
                    uiUtils.showToast("获取验证码失败");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                uiUtils.showToast("失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                UiUtils.showToast("cancelled");
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
    }

    Timer timer ;
    private TimeTask timeTask ;
    private int release;

    //开始倒计时
    private void startTime() {
        timer = new Timer();
        timeTask = new TimeTask();
        release = 60;
        timer.schedule(timeTask, 0, 1000);       // timeTask
        getCode.setClickable(false);
        getCode.setText(release+"秒");
        getCode.setBackgroundResource(R.drawable.button_fast_nor);

    }
    class TimeTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    release--;
                    getCode.setText(release+"秒");
                    if(release < 0){
                        timer.cancel();
                        timeTask.cancel();
                        getCode.setClickable(true);
                        getCode.setBackgroundResource(R.drawable.button_fast_pre);
                        getCode.setText("获取验证码");
                    }
                }
            });
        }
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

}
  
  
  
  
