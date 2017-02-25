package com.joshua.a51bike.activity.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.joshua.a51bike.Interface.UserState;
import com.joshua.a51bike.activity.view.Login;
import com.joshua.a51bike.activity.view.register;
import com.joshua.a51bike.activity.view.registerOrLogin;
import com.joshua.a51bike.util.AppUtil;

/**
 * Created by wangqiang on 2017/1/9.
 */

public class LogoutState implements UserState {
    @Override
    public void toChoice(Activity activity) {
        Choice(activity);

    }

    @Override
    public void login(Activity activity) {
        Intent intent = new Intent(activity, Login.class);
        activity.startActivityForResult(intent, AppUtil.INTENT_REQUSET);
    }

    @Override
    public void register(Activity activity) {
        Intent intent = new Intent(activity, register.class);
        activity.startActivityForResult(intent, AppUtil.INTENT_REQUSET);

    }

    @Override
    public void getUserIcn(Context context) {

    }

    @Override
    public void account(Activity activity) {
        Choice(activity);

    }

    @Override
    public void saoma(Activity activity) {
        Choice(activity);
    }

    @Override
    public void toBikeMes(Activity activity,String url) {
        Choice(activity);


    }

    @Override
    public void rent(Activity activity) {
        Choice(activity);

    }

    @Override
    public void service(Activity activity) {
        Choice(activity);


    }

    @Override
    public void share(Activity activity) {
        Choice(activity);

    }

    @Override
    public void reCharge(Activity activity) {
        Choice(activity);

    }

    @Override
    public void returnBike(Activity activity) {
        Choice(activity);

    }

    @Override
    public void lockBike(Activity activity) {
        Choice(activity);


    }

    @Override
    public void startBike(Activity activity) {
        Choice(activity);

    }

    @Override
    public void pay(Activity activity) {
        Choice(activity);

    }

    private void Choice(Activity activity){
        Intent intent = new Intent(activity, registerOrLogin.class);
//        Intent intent = new Intent(activity, aaa.class);

        activity.startActivity(intent);
    }
}
