package com.joshua.a51bike.activity.view;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import static com.joshua.a51bike.R.layout.login;


/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */
@ContentView(login)
public class Login extends BaseActivity{
    private String TAG = "Login";
    private String url = AppUtil.BaseUrl + "/user/login";
    private EditText getName, getPass;
    private Button back;
    private Button login;

    private Handler handler = new Handler() {
        public void handleMessage(Message mes){
            switch (mes.what){
                case NET_SUCCESS:
                    uiUtils.showToast("成功登陆！");
                    dialogControl.cancel();
                    break;
                case NET_ERROR:
                    uiUtils.showToast("登陆失败！");
                    dialogControl.cancel();
                    break;
                default:
                    break;
            }
        }
    };

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
        getName = (EditText) findViewById(R.id.login_Admin);
        getPass = (EditText) findViewById(R.id.login_Pass);
        login = (Button) findViewById(R.id.login_button);
        getName.setText("wei");
        getPass.setText("123456");
    }

    public void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);
//        findViewById(R.id.Login_register).setOnClickListener(this);
        login.setOnClickListener(this);
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
            case R.id.login_button:
                Login();
                break;
            default:
                break;
        }
    }
    private void Login(){
        if (MyTools.EditTextIsNull(getName) || MyTools.EditTextIsNull(getPass)) {
            return;
        }
        String name = getName.getText().toString();
        String pass = getPass.getText().toString();

        User user = new User();
        user.setUserpass(pass);
        user.setUsername(name);
        RequestParams params = new RequestParams(url);

        params.addBodyParameter("username",name);
        params.addBodyParameter("userpass",pass);
        post(params);
//        postJson(params);

        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
    }

    private void postJson( RequestParams params){
        Log.d(TAG, "postJson: begin to post json");
          Callback.Cancelable cancelable
                = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: result is"+result );
                Toast.makeText(x.app(), result.toString(), Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dialogControl.cancel();
                Log.e(TAG, "onError: error !!!", null);
                ex.printStackTrace();
//                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    Log.e(TAG, "onError: mes is "+responseMsg +" error "+errorResult, null);
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                dialogControl.cancel();

                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                dialogControl.cancel();

            }
        });
    }
    private void post(RequestParams params){
        Log.d(TAG, "post: post by xutils------>>");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "login come from Manager response is " + result.toString());
                User user = JsonUtil.getUserObject(result.toString());
                if(null != user){
                    userControl.setUser(user);
                    uiUtils.showToast("登陆成功！");
                    userControl.setUserState(new LoginState());
                    setResult(AppUtil.INTENT_RESPONSE);
                    finish();
                }else
                    uiUtils.showToast("登陆失败！");
                dialogControl.cancel();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//              handler.sendEmptyMessage(NET_ERROR);
                Log.e(TAG, "onError: onError", null);
                uiUtils.showToast("登陆失败！");
                dialogControl.cancel();

                ex.printStackTrace();
//                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    Log.e(TAG, "onError: mes is " + responseMsg + " error " + errorResult, null);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: cancel", null);
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
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