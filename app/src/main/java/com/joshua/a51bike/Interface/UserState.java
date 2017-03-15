package com.joshua.a51bike.Interface;

import android.app.Activity;
import android.content.Context;

/**
 * Created by wangqiang on 2017/1/9.
 */

public interface UserState {
    void toChoice(Activity activity);
    void login(Activity activity);
    void register(Activity activity);
    void getUserIcn(Context context);

    void saoma(Activity activity);
    void toBikeMes(Activity activity,String url);
    void rent(Activity activity);

    void share(Activity activity);
    void reCharge(Activity activity);
    void returnBike(Activity activity);
    void lockBike(Activity activity);
    void startBike(Activity activity);
    void pay(Activity activity);

    void userRoute(Activity activity);
    void userInfor(Activity activity);
    void renZheng(Activity activity);
    void userInforPhoneBefor(Activity activity);
    void account(Activity activity);
    void accountMiXi(Activity activity);

    void accountRecharge(Activity activity);
    void accountYaJin(Activity activity);

    void service(Activity activity);

    void config(Activity activity);
    void configSuggest(Activity activity);
    void configAbout(Activity activity);
}
