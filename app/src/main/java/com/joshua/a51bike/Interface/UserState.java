package com.joshua.a51bike.Interface;

import android.app.Activity;
import android.content.Context;

/**
 * 用户状态管理接口
 *
 * Created by wangqiang on 2017/1/9.
 */

public interface UserState {

    //登陆注册二选一
    void toChoice(Activity activity);

    //登陆控制
    void login(Activity activity);

    //获取用户详情控制
    void getUserIcn(Context context,String filePath);

    //扫码控制
    void saoma(Activity activity);

    //跳转到bike message界面控制
    void toBikeMes(Activity activity,String url);

    //用户点击租车控制
    void rent(Activity activity);

    //用户分享控制
    void share(Activity activity);

    //用户充值控制
    void reCharge(Activity activity);

    //用户还车控制
    void returnBike(Activity activity);

    //用户锁车控制
    void lockBike(Activity activity);

    //用户启动车辆控制
    void startBike(Activity activity);

    //用户支付费用控制
    void pay(Activity activity);

    //用户路径列表控制
    void userRoute(Activity activity);

    //用户信息详情控制
    void userInfor(Activity activity);

    //用户认证控制
    void renZheng(Activity activity);

    //用户修改电话号码控制
    void userInforPhoneBefor(Activity activity);

    //用户账号余额细控制
    void account(Activity activity);

    //用户充值明细控制
    void accountMiXi(Activity activity);

    //用户充值控制
    void accountRecharge(Activity activity);

    //用户交押金控制
    void accountYaJin(Activity activity);

    //用户联系客服控制
    void service(Activity activity);

    //系统配置控制
    void config(Activity activity);

    //用户反馈控制
    void configSuggest(Activity activity);

    //关于应用控制
    void configAbout(Activity activity);
}
