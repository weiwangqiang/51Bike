package com.joshua.a51bike.activity.view;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.LoginState;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.JsonUtil;
import com.joshua.a51bike.util.MyTools;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;


/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */
@ContentView(R.layout.login)
public class Login extends BaseActivity{
    private String TAG = "Login";
    private String url = AppUtil.BaseUrl +"/user/login";
    private String mesUrl = AppUtil.BaseUrl +"/user/getCode";
    private String resultCode = "";
    private int type = 0;
    private static final int GET_CODE = 1;
    private static final int LOGIN = 2;
    private EditText getName, getCode;
    private   User user;

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
        getName.setText("18852852276");
//        getCode.setText("666666");
    }

    public void setLister() {
        findViewById(R.id.button_fast_login).setOnClickListener(this);
        findViewById(R.id.get_code).setOnClickListener(this);
        findViewById(R.id.bike_mes_agreement).setOnClickListener(this);

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


    public void getCode ( ){
        if(MyTools.EditTextIsNull(getName))
            return ;
//        if(!MyTools.isMobileNO(getName.getText().toString())){
//            ToastUtil.show(Login.this,"请输入正确的手机号");
//            return;
//        }
        type = GET_CODE;
        RequestParams params = new RequestParams(mesUrl);
        params.addBodyParameter("phoneNumber",getName.getText().toString());
        Log.i(TAG, "getCode: url code is "+params.toString());
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
        post(params);

    }
    private void login(){
//        if (MyTools.EditTextIsNull(getName) || MyTools.EditTextIsNull(getCode)) {
//            ToastUtil.show(Login.this,"手机号或验证码不能为空~");
//            return;
//        }
        type = LOGIN;
        String name = getName.getText().toString();
        String code = getCode.getText().toString();
//        if(!code.equals(resultCode)){
//            ToastUtil.show(Login.this,"验证码出错了~");
//            return;
//        }
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("username",name);
        user = new User();
        user.setUsername(name);
        post(params);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
    }
    private   Callback.Cancelable cancelable;
    private void post(RequestParams params){
         cancelable =  x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                Log.d(TAG, "success: object is "+ result.toString());
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
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
    }
    //登陆成功
    private void loginSuccess(String result) {
        Log.i(TAG, "loginSuccess: userima"+result);
        User user = JsonUtil.getUserObject(result);
        if(user != null){
            Log.i(TAG, "loginSuccess: id"+user.getUserid());
            uiUtils.showToast("成功！");
            userControl.setUser(user);
            userControl.setUserState(new LoginState());
            setResult(AppUtil.INTENT_RESPONSE);
            finish();
        }else   uiUtils.showToast("失败！");
    }
    //获取验证码成功
    private void getCodeSuccess(String result) {
        Log.i(TAG, "getCodeSuccess: "+result);
        if(result.length() == 6 ){
            resultCode = result;
            uiUtils.showToast("已发送验证码，请注意查收");
        }else   uiUtils.showToast("获取验证码失败");
    }

    @Override
    public void onBackPressed() {
        if((cancelable != null) && (!cancelable.isCancelled())){
            cancelable.cancel();
            Log.i(TAG, "onBackPressed: be cancel");
        }
        else{
            finish();
            Log.i(TAG, "onBackPressed: finish");
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