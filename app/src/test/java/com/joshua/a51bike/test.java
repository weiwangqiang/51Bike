package com.joshua.a51bike;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-05-04
 */
public class test {
    private String TAG = "test";

    public test() {
    }
    public static void main(String args[]){
   String url = "http://123.206.104.107/diandongche/car/getGps";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("id","E899B6C8A9B9");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.print(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }
}
