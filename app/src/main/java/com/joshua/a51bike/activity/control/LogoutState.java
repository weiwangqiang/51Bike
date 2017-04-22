package com.joshua.a51bike.activity.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.joshua.a51bike.Interface.UserState;
import com.joshua.a51bike.activity.view.Config;
import com.joshua.a51bike.activity.view.Login;
import com.joshua.a51bike.activity.view.UserRoute;
import com.joshua.a51bike.activity.view.about;
import com.joshua.a51bike.activity.view.registerOrLogin;
import com.joshua.a51bike.util.AppUtil;

/**
 * 用户退出登陆状态
 *
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
    public void getUserIcn(Context context,String filePath) {

    }

    @Override
    public void account(Activity activity) {
        Choice(activity);

    }

    @Override
    public void accountMiXi(Activity activity) {
        Choice(activity);
    }

    @Override
    public void accountRecharge(Activity activity) {
        Choice(activity);

    }

    @Override
    public void accountYaJin(Activity activity) {
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

    @Override
    public void config(Activity activity) {
        Intent intent = new Intent(activity, Config.class);
        activity.startActivity(intent);
    }

    @Override
    public void configSuggest(Activity activity) {
        Choice(activity);

    }

    @Override
    public void configAbout(Activity activity) {
        Intent intent = new Intent(activity, about.class);
        activity.startActivity(intent);
    }

    @Override
    public void userRoute(Activity activity) {
//        Choice(activity);
        Intent intent = new Intent(activity, UserRoute.class);
        activity.startActivity(intent);
    }

    @Override
    public void userInfor(Activity activity) {
//        Choice(activity);

    }

    @Override
    public void renZheng(Activity activity) {
        Choice(activity);

    }

    @Override
    public void userInforPhoneBefor(Activity activity) {
        Choice(activity);

    }
    //登陆，注册 二选一界面
    private void Choice(Activity activity){
        Intent intent = new Intent(activity, registerOrLogin.class);
        activity.startActivity(intent);
    }
}
