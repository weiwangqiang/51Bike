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
    void account(Activity activity);
    void saoma(Activity activity);
    void toBikeMes(Activity activity);
    void rent(Activity activity);
    void service(Activity activity);
    void share(Activity activity);
    void reCharge(Activity activity);
    void returnBike(Activity activity);
    void lockBike(Activity activity);
    void startBike(Activity activity);
    void pay(Activity activity);
}
