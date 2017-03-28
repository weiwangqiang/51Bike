package com.joshua.a51bike.activity.view;

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
import com.joshua.a51bike.util.MyTools;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * class description here
 *  注册
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */
@ContentView(R.layout.login_fast)
public class register extends BaseActivity {
    private String TAG = "register";
    private String url = AppUtil.BaseUrl +"/user/insertUser";
    private String mesUrl = AppUtil.BaseUrl +"/user/getCode";

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
//        getName.setText("18852852276");
//        getCode.setText("666666");
    }

    public void setLister() {
        findViewById(R.id.button_fast_login).setOnClickListener(this);
        findViewById(R.id.get_code).setOnClickListener(this);


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
            default:
                break;
        }
    }
    public void getCode ( ){

        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();

        Log.i(TAG, "getCode: ");
        if(MyTools.EditTextIsNull(getName))
            return ;
        if(!MyTools.isMobileNO(getName.getText().toString())){
            Log.i(TAG, "getCode: error!");
            return;
        }

        RequestParams params = new RequestParams(mesUrl);
        params.addBodyParameter("phoneNumber",getName.getText().toString());
        Log.i(TAG, "getCode: url code is "+params.toString());
        post(params);

    }
    private void login(){

        if (MyTools.EditTextIsNull(getName) || MyTools.EditTextIsNull(getCode)) {
            return;
        }
        String name = getName.getText().toString();
        String code = getCode.getText().toString();
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("username",name);
        params.addBodyParameter("userpass",code);
         user = new User();
        user.setUsername(name);
        user.setUserpass(code);
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

                if(result.equals("true")){
                    uiUtils.showToast("注册成功！");
                    userControl.setUser(user);
                    userControl.setUserState(new LoginState());
                    setResult(AppUtil.INTENT_RESPONSE);
                    finish();
                }else   uiUtils.showToast("注册失败！");


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                uiUtils.showToast("注册失败！");
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


}