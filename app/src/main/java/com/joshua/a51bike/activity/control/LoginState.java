package com.joshua.a51bike.activity.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.joshua.a51bike.Interface.UserState;
import com.joshua.a51bike.activity.dialog.GetIcnAlerDialog;
import com.joshua.a51bike.activity.dialog.LocateProgress;
import com.joshua.a51bike.activity.view.BikeControl;
import com.joshua.a51bike.activity.view.BikeMessage;
import com.joshua.a51bike.activity.view.BlueTooth;
import com.joshua.a51bike.activity.view.Pay;
import com.joshua.a51bike.activity.view.Recharge;
import com.joshua.a51bike.activity.view.register;
import com.joshua.a51bike.activity.view.renzheng;

/**
 * Created by wangqiang on 2017/1/9.
 */

public class LoginState implements UserState {
    private String TAG = "LoginState";
    private DialogControl dialogControl;
    public LoginState(){
        dialogControl = DialogControl.getDialogControl();
    }

    @Override
    public void toChoice(Activity activity) {
        Intent intent = new Intent(activity, renzheng.class);
        activity.startActivity(intent);
    }

    @Override
    public void login(Activity activity) {
//        Intent intent = new Intent(activity, renzheng.class);
//        activity.startActivity(intent);
    }

    @Override
    public void register(Activity activity) {
        Intent intent = new Intent(activity, register.class);
        activity.startActivity(intent);
    }

    @Override
    public void getUserIcn(Context context) {
        dialogControl.setDialog(new GetIcnAlerDialog(context));
        dialogControl.show();
    }

    @Override
    public void account(Activity activity) {

    }

    @Override
    public void saoma(Activity activity) {
//        Intent intent = new Intent(activity, ScanActivity.class);
        Intent intent = new Intent(activity, BlueTooth.class);

        activity.startActivity(intent);
//        dialogControl.setDialog(new MarginAlerDialog(activity,"保证金提示","请先充值保证金"));
//        dialogControl.show();
    }

    @Override
    public void toBikeMes(Activity activity,String url) {

        Intent intent = new Intent(activity, BikeMessage.class);
        intent.putExtra("url",url);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void rent(Activity activity) {
        Intent intent = new Intent(activity, BikeControl.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void service(Activity activity) {

    }

    @Override
    public void share(Activity activity) {

    }

    @Override
    public void reCharge(Activity activity) {
        Intent intent = new Intent(activity, Recharge.class);
        activity.startActivity(intent);
    }

    @Override
    public void returnBike(Activity activity) {
        Intent intent = new Intent(activity, Pay.class);
        activity.startActivity(intent);
    }

    @Override
    public void lockBike(Activity activity) {
        dialogControl.setDialog(new LocateProgress(activity,"正在锁车"));
        dialogControl.show();
    }

    @Override
    public void startBike(Activity activity) {
        dialogControl.setDialog(new LocateProgress(activity,"正在启动车"));
        dialogControl.show();
    }

    @Override
    public void pay(Activity activity) {

    }
}
