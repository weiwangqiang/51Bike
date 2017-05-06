package com.joshua.a51bike.activity.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.joshua.a51bike.Interface.DialogCallBack;
import com.joshua.a51bike.Interface.UserState;
import com.joshua.a51bike.activity.dialog.CurrencyAlerDialog;
import com.joshua.a51bike.activity.dialog.GetIcnAlerDialog;
import com.joshua.a51bike.activity.dialog.LocateProgress;
import com.joshua.a51bike.activity.view.AccountYaJin;
import com.joshua.a51bike.activity.view.BikeControlActivity;
import com.joshua.a51bike.activity.view.BikeMessage;
import com.joshua.a51bike.activity.view.Config;
import com.joshua.a51bike.activity.view.Pay;
import com.joshua.a51bike.activity.view.RZRealName;
import com.joshua.a51bike.activity.view.ScanActivity;
import com.joshua.a51bike.activity.view.Suggest;
import com.joshua.a51bike.activity.view.UserInfor;
import com.joshua.a51bike.activity.view.UserInforPhoneBefor;
import com.joshua.a51bike.activity.view.UserRoute;
import com.joshua.a51bike.activity.view.about;
import com.joshua.a51bike.activity.view.account;
import com.joshua.a51bike.activity.view.accountMingxi;
import com.joshua.a51bike.activity.view.accountRecharge;
import com.joshua.a51bike.activity.view.share;

/**
 * 用户登陆状态
 *
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
            this.userInfor(activity);
    }

    @Override
    public void login(Activity activity) {
//        Intent intent = new Intent(activity, renzheng.class);
//        activity.startActivity(intent);
    }

    @Override
    public void getUserIcn(Context context,String filePath) {
        dialogControl.setDialog(new GetIcnAlerDialog(context ,filePath));
        dialogControl.show();
    }
    //钱包
    @Override
    public void account(Activity activity) {

        Intent intent = new Intent(activity, account.class);
        activity.startActivity(intent);
    }

    @Override
    public void accountMiXi(Activity activity) {
        Intent intent = new Intent(activity, accountMingxi.class);
        activity.startActivity(intent);
    }

    @Override
    public void accountRecharge(Activity activity) {
        Intent intent = new Intent(activity, accountRecharge.class);
        activity.startActivity(intent);
    }

    @Override
    public void accountYaJin(Activity activity) {
        Intent intent = new Intent(activity, AccountYaJin.class);
        activity.startActivity(intent);
    }

    @Override
    public void saoma(Activity activity) {
        Intent intent ;
        if(UserControl.getUserControl().getUser().getRealName()== null){
            //没有进行实名认证
            intent = new Intent(activity, RZRealName.class);
        }
        else if(UserControl.getUserControl().getUser().getUserRerve()<200)
            //没有充值保证金
           intent = new Intent(activity, AccountYaJin.class);
        else
            //进入扫码界面
          intent = new Intent(activity, ScanActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void toBikeMes(Activity activity,String bike_mac) {

        Intent intent = new Intent(activity, BikeMessage.class);
        intent.putExtra("bike_mac",bike_mac);
        activity.startActivity(intent);
        activity.finish();
    }
    //租车
    @Override
    public void rent(Activity activity) {
        Intent intent = new Intent(activity, BikeControlActivity.class);
        intent.putExtra("bid","2634");
        intent.putExtra("from_where","normal");
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void service(final Activity activity) {

        DialogCallBack call = new DialogCallBack(){
            @Override
            public void sure() {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + "18852852276");
                intent.setData(data);
                activity.startActivity(intent);
            }

            @Override
            public void cancel() {

            }
        };
        CurrencyAlerDialog dialog = new CurrencyAlerDialog(activity,"提示","确定拨打客服电话？",call);
        dialog.myShow();
    }

    @Override
    public void share(Activity activity) {
        Intent intent = new Intent(activity, share.class);
        activity.startActivity(intent);
    }

    @Override
    public void reCharge(Activity activity) {
        Intent intent = new Intent(activity, accountRecharge.class);
        activity.startActivity(intent);
    }

    @Override
    public void returnBike(Activity activity) {
        Intent intent = new Intent(activity, Pay.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void lockBike(Activity activity) {

    }

    @Override
    public void startBike(Activity activity) {
        dialogControl.setDialog(new LocateProgress(activity,"正在启动车"));
        dialogControl.show();
    }

    @Override
    public void pay(Activity activity) {

    }

    @Override
    public void config(Activity activity) {
        Intent intent = new Intent(activity, Config.class);
        activity.startActivity(intent);
    }

    @Override
    public void configSuggest(Activity activity) {
        Intent intent = new Intent(activity, Suggest.class);
        activity.startActivity(intent);
    }

    @Override
    public void configAbout(Activity activity) {
        Intent intent = new Intent(activity, about.class);
        activity.startActivity(intent);
    }
    //用户路程
    @Override
    public void userRoute(Activity activity) {
        Intent intent = new Intent(activity, UserRoute.class);
        activity.startActivity(intent);

    }

    @Override
    public void userInfor(Activity activity) {
        Intent intent = new Intent(activity, UserInfor.class);
        activity.startActivity(intent);
    }

    @Override
    public void renZheng(Activity activity) {
        Intent intent = new Intent(activity, RZRealName.class);
        activity.startActivity(intent);
    }

    @Override
    public void userInforPhoneBefor(Activity activity) {
        Intent intent = new Intent(activity, UserInforPhoneBefor.class);
        activity.startActivity(intent);
    }
}
