package com.joshua.a51bike.pay.util;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.joshua.a51bike.Interface.PaySuccess;
import com.joshua.a51bike.util.UiUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-29
 */
public class PayUtils {
    private  String TAG = "PayUtils";
    public  final String APPID = "2017020605540725";
    private PaySuccess paySuccess;
    public  static PayUtils payUtils = new PayUtils();
    public PayUtils() {
    }
    public void setPaySuccess(PaySuccess paySuccess){
        this.paySuccess = paySuccess;
    }
    public static  PayUtils getPayUtils(){
        return payUtils;
    }
    /**
     * 支付宝支付业务
     *
     *
     */
    public void payV2(final Activity activity,float money) {

        Log.i(TAG, "payV2: ");
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID,money, false);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        Log.i("msp", ">>>>>>>>>>>>>>>>>>>>>>>>>>orderParam:"+params.toString());
        /**
         * 访问服务器
         * 上传订单信息
         */
        String url="http://123.206.104.107/diandongche/user/getOrder";

        RequestParams order_params = new RequestParams(url);
        order_params.addParameter("app_id",params.get("app_id"));
        order_params.addParameter("biz_content",params.get("biz_content"));
        order_params.addParameter("charset",params.get("charset"));
        order_params.addParameter("method",params.get("method"));
        order_params.addParameter("sign_type",params.get("sign_type"));
        order_params.addParameter("timestamp",params.get("timestamp"));
        order_params.addParameter("version",params.get("version"));
        order_params.addParameter("orderParam",orderParam);
        x.http().post(order_params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final String orderInfo = result.trim();
                Log.i("msp","orderInfo:"+result);
                Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(activity);
                        Map<String, String> result = alipay.payV2(orderInfo, true);
                        Log.d("msp", "<<<<<<<<<<<<payResult>>>>>>>>>>: "+result.toString());
                        postPayResult(result);

                    }
                };

                Thread payThread = new Thread(payRunnable);
                payThread.start();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("msp", ">>>>>>>>>>>>>>>>>>>>>>>>>>:"+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 将支付结果发送给服务器
     */
    private void postPayResult(Map<String, String > result) {
        if(result.get("resultStatus").equals("6001")){
            UiUtils.showToast("操作已经取消");
        }
        Log.i(TAG, "postPayResult: ");
        String url="http://123.206.104.107/diandongche/user/verifyResult";
        RequestParams result_params = new RequestParams(url);
        result_params.addParameter("resultStatus",result.get("resultStatus"));
        result_params.addParameter("result",result.get("result"));
        result_params.addParameter("memo",result.get("memo"));


        x.http().post(result_params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (TextUtils.equals(result, "9000")) {
                    paySuccess.onSccuess();
                } else {
                    UiUtils.showToast("支付失败");

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("msp", ">>>>>>>>>>>>>>>>>>>>>>>>>>o2:"+ex.getMessage());
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
