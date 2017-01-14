package com.joshua.a51bike.activity.control;

import android.app.Activity;

import com.joshua.a51bike.Interface.UserState;

/**
 * Created by wangqiang on 2017/1/9.
 */

public class UserControl implements UserState {
    private  static UserControl userControl = new UserControl();
    private  UserState userState = new LoginState();
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
    public void register(Activity activity) {
        userState.register(activity);
    }

    @Override
    public void account(Activity activity) {
        userState.account(activity);
    }

    @Override
    public void saoma(Activity activity) {
        userState.saoma(activity);
    }

    @Override
    public void toBikeMes(Activity activity) {
        userState.toBikeMes(activity);

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
}
