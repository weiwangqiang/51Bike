package com.joshua.a51bike.activity;

import android.util.Log;

import com.joshua.a51bike.util.AppUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-04-09
 */
public class test {
    private static  String TAG = "test";
    private static  String url = AppUtil.BaseUrl+"/car/getGps";

    public test() {
    }
    public static void main(String arg0[]){
        RequestParams params = new RequestParams(url);
//        params.addBodyParameter("carId","EA8F2B98C3E8FFFFFFFFFFFF");
        post(params);
    }
    /*发送请求*/
    private static void post(RequestParams params){
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//              1handler.sendEmptyMessage(NET_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: cancel", null);

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
