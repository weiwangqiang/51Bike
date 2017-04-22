package com.joshua.a51bike.activity.control;

import android.app.Activity;
import android.content.Context;

import com.joshua.a51bike.Interface.UserState;
import com.joshua.a51bike.entity.User;

/**
 * 用户控制类
 *
 * Created by wangqiang on 2017/1/9.
 */

public class UserControl implements UserState {
    private  static UserControl userControl = new UserControl();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private  UserState userState = new LogoutState();
    private User user = null ;
    public static UserControl getUserControl(){
        return  userControl;
    }
    public void setUserState(UserState userState){
        this.userState = userState;
    }

    @Override
    public void toChoice(Activity activity) {
        userState.toChoice(activity);
    }

    @Override
    public void login(Activity activity) {
        userState.login(activity);
    }


    @Override
    public void getUserIcn(Context context,String filePath) {
        userState.getUserIcn(context,filePath);
    }

    @Override
    public void account(Activity activity) {
        userState.account(activity);
    }

    @Override
    public void accountMiXi(Activity activity) {
        userState.accountMiXi(activity);

    }

    @Override
    public void accountRecharge(Activity activity) {
        userState.accountRecharge(activity);
    }

    @Override
    public void accountYaJin(Activity activity) {
        userState.accountYaJin(activity);
    }

    @Override
    public void saoma(Activity activity) {
        userState.saoma(activity);
    }

    @Override
    public void toBikeMes(Activity activity,String url) {
        userState.toBikeMes(activity,url);

    }

    @Override
    public void rent(Activity activity) {
        userState.rent(activity);
    }

    @Override
    public void service(Activity activity) {
        userState.service(activity);
    }

    @Override
    public void share(Activity activity) {
        userState.share(activity);
    }

    @Override
    public void reCharge(Activity activity) {
        userState.reCharge(activity);
    }

    @Override
    public void returnBike(Activity activity) {
        userState.returnBike(activity);
    }

    @Override
    public void lockBike(Activity activity) {
        userState.lockBike(activity);
    }

    @Override
    public void startBike(Activity activity) {
        userState.startBike(activity);
    }

    @Override
    public void pay(Activity activity) {
        userState.pay(activity);
    }

    @Override
    public void config(Activity activity) {
        userState.config(activity);
    }

    @Override
    public void configSuggest(Activity activity) {
        userState.configSuggest(activity);
    }

    @Override
    public void configAbout(Activity activity) {
        userState.configAbout(activity);
    }

    @Override
    public void userRoute(Activity activity) {
        userState.userRoute(activity);
    }

    @Override
    public void userInfor(Activity activity) {
        userState.userInfor(activity);
    }

    @Override
    public void renZheng(Activity activity) {
        userState.renZheng(activity);
    }

    @Override
    public void userInforPhoneBefor(Activity activity) {
        userState.userInforPhoneBefor(activity);
    }
}
