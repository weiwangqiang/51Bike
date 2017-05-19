package com.joshua.a51bike.activity.view;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.LoginState;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.application.BaseApplication;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.JsonUtil;
import com.joshua.a51bike.util.MyTools;
import com.joshua.a51bike.util.PrefUtils;
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
 *  登陆界面
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */
@ContentView(R.layout.login)
public class Login extends BaseActivity  {
    private String TAG = "Login";
    private String url = AppUtil.BaseUrl +"/user/login";
    private String mesUrl = AppUtil.BaseUrl +"/user/getCode";
    private String resultCode = "";
    private int type = 0;
    private static final int GET_CODE = 1;
    private static final int LOGIN = 2;
    private EditText getName, getCode;
    private   User user;

    @ViewInject(R.id.get_code)
    private TextView get_code;


    @ViewInject(R.id.button_fast_login)
    private TextView button_fast_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    public void init() {
        findId();
        setLister();
    }


    public void findId() {
        getName = (EditText) findViewById(R.id.login_fast_admin);
        getCode = (EditText) findViewById(R.id.login_fast_code);
//        getName.setText("15977086991");
//        getName.setText("18852852276");
//        getCode.setText("666666");
    }

    public void setLister() {
        button_fast_login.setOnClickListener(this);
        get_code.setOnClickListener(this);
        getCode.addTextChangedListener(new myTextWatcher());
        findViewById(R.id.bike_mes_agreement).setOnClickListener(this);

    }
    public class myTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() > 0){
                button_fast_login.setBackgroundResource(R.drawable.button_fast_pre);
                button_fast_login.setClickable(true);
            }else{
                button_fast_login.setBackgroundResource(R.drawable.button_fast_nor);
                button_fast_login.setClickable(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

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
            case R.id.button_fast_login:
                login();
                break;
            case R.id.get_code:
                getCode();
                break;
            case R.id.bike_mes_agreement:
                toWebView();
                break;
            default:
                break;
        }
    }

    public void toWebView(){
        Intent intent = new Intent(this,RegisterAgreement.class);
        intent.putExtra("title","51get租车服务条款");
        intent.putExtra("url","51get租车服务条款");
        startActivity(intent);
    }


    public void getCode (){
        if(MyTools.EditTextIsNull(getName)){
           UiUtils.showToast("手机号不能为空");
            return ;
        }
        if(!MyTools.isMobileNO(getName.getText().toString())){
            UiUtils.showToast("请输入正确的手机号");
            return;
        }
        type = GET_CODE;
        RequestParams params = new RequestParams(mesUrl);
        params.addBodyParameter("phoneNumber",getName.getText().toString());
        Log.i(TAG, "getCode: url code is "+params.toString());
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
        post(params);

    }
    private void login(){
        if (MyTools.EditTextIsNull(getName)) {
            UiUtils.showToast("手机号不能为空~");
            return;
        }
        if (MyTools.EditTextIsNull(getCode)) {
            UiUtils.showToast("验证码不能为空~");
            return;
        }

        type = LOGIN;
        String name = getName.getText().toString();
        String code = getCode.getText().toString();
//        if(!code.equals(resultCode)){
//            UiUtils.showToast("验证码出错了~");
//            return;
//        }
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("username",name);
        user = new User();
        user.setUsername(name);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
        post(params);
    }
    private   Callback.Cancelable cancelable;
    private void post(RequestParams params){
         cancelable =  x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dialogControl.cancel();
                switch (type){
                    case GET_CODE:
                        getCodeSuccess(result);
                        break;
                    case LOGIN:
                        loginSuccess(result);
                        break;
                }

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
    //登陆成功
    private void loginSuccess(String result) {
        Log.i(TAG, "loginSuccess: "+result);
        User user = JsonUtil.getUserObject(result);
        if(user != null){
            uiUtils.showToast("成功！");
            userControl.setUser(user);
            userControl.setUserState(new LoginState());
            setResult(AppUtil.INTENT_RESPONSE);
            PrefUtils.setString(BaseApplication.getApplication(),PrefUtils.USER_NAME,user.getUsername());
            finish();
        }else   uiUtils.showToast("失败！");
    }
    //获取验证码成功
    private void getCodeSuccess(String result) {
        Log.i(TAG, "getCodeSuccess: "+result);
        if(result.length() == 6 ){
            resultCode = result;
            startTime();
            uiUtils.showToast("已发送验证码，请注意查收");
        }else
            uiUtils.showToast("获取验证码失败");
    }

    Timer timer ;
    private TimeTask timeTask ;
    private int release;

    //开始倒计时
    private void startTime() {
        timer = new Timer();
        timeTask = new TimeTask();
        release = 60;
        timer.schedule(timeTask, 1000, 1000);       // timeTask
        get_code.setClickable(false);
        get_code.setText(release+"秒");
        get_code.setBackgroundResource(R.drawable.button_fast_nor);

    }
    class TimeTask extends TimerTask{

        @Override
        public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    release--;
                    get_code.setText(release+"秒");
                    if(release < 0){
                        timer.cancel();
                        timeTask.cancel();
                        get_code.setClickable(true);
                        get_code.setBackgroundResource(R.drawable.button_fast_pre);
                        get_code.setText("获取验证码");
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if((cancelable != null) && (!cancelable.isCancelled())){
            cancelable.cancel();
        }
        else{
            finish();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}