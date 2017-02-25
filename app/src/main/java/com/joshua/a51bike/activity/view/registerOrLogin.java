package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.util.AppUtil;

import org.xutils.view.annotation.ContentView;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-13
 */
@ContentView(R.layout.login_or_register)
public class registerOrLogin extends BaseActivity {
    private String TAG = "registerOrLogin" ;
    private UserControl userControl;

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
//        findViewById(R.id.left_back).setOnClickListener(this);
        findViewById(R.id.Register_button).setOnClickListener(this);
        findViewById(R.id.Login_button).setOnClickListener(this);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Login_button:
                userControl.login(registerOrLogin.this);
                break;
            case R.id.Register_button:
                userControl.register(registerOrLogin.this);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult (int requestCode,
                                     int resultCode,
                                     Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == AppUtil.INTENT_REQUSET ){
            switch (resultCode){
                case  AppUtil.INTENT_RESPONSE:
                    finish();
                    break;
                case RESULT_CANCELED :
                    Log.i(TAG, "onActivityResult: fail");
                    break;
                default:
                    break;
            }
        }
    }
}