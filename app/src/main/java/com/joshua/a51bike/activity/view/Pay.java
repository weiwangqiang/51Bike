package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.joshua.a51bike.Interface.PaySuccess;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.pay.util.PayUtils;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.UiUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by wangqiang on 2017/1/9.
 *
 *  用户使用车辆，还车后的支付界面
 *
 */
@ContentView(R.layout.pay)
public class Pay extends BaseActivity {
    private String TAG = "Pay";
    private String url_fukuan = AppUtil.BaseUrl +"/user/fukuan";
    private String url_isQianfei = AppUtil.BaseUrl +"/user/isQianfei";
    private int money = 100;
    @ViewInject(R.id.order_number)
    private TextView order_number;

    @ViewInject(R.id.useMoney)
    private TextView useMoney;

    @ViewInject(R.id.bike_id)
    private TextView bike_id;

    @ViewInject(R.id.pay_or_not)
    private TextView pay_or_not;

    @ViewInject(R.id.pay_button)
    private TextView pay_button;

    @ViewInject(R.id.pay_RadioGroup)
    private RadioGroup pay_RadioGroup;

    @ViewInject(R.id.order_startTime)
    private TextView order_startTime;

    @ViewInject(R.id.order_endTime)
    private TextView order_endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void setpayViewUnVisible() {
        pay_button.setVisibility(View.GONE);
        pay_RadioGroup.setVisibility(View.GONE);
    }
    private void setpayViewVisible() {
        pay_button.setVisibility(View.VISIBLE);
        pay_RadioGroup.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //使用余额支付
        Log.i(TAG, "onResume: 我来了------------------------");
        if(userControl.getUser().getUsermoney() >0){
            Log.i(TAG, "pay:使用余额 ============");
            setpayViewUnVisible();
            post(TYPE_USEMON);
        }
    }

    public void init() {
        initActionBar();
        setLister();
        initPayMes();
    }

    /**
     * 初始化订单信息
     */
    private void initPayMes() {
        order_number.setText(userControl.getOrder().getCarId()+"");
        bike_id.setText(userControl.getOrder().getCarId()+"");
        useMoney.setText("应付金额 ："+ (userControl.getOrder().getUseMoney())+" 元");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        order_startTime.setText(format.format(userControl.getOrder().getUseStartTime()));
        order_endTime.setText(format.format(userControl.getOrder().getUseEndTime()));
    }

    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    private void setLister() {
        findViewById(R.id.pay_button).setOnClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @ViewInject(R.id.pay_zhifubao)
    private RadioButton zhifubao;

    @ViewInject(R.id.pay_weixin)
    private RadioButton weixin;

    private void pay() {
        Log.i(TAG, "pay: =====================");
        if(zhifubao.isChecked()){
            uiUtils.showToast("正在跳转");
            PayUtils payUtils = PayUtils.getPayUtils();
            payUtils.setPaySuccess(new myPaySuccess());
            payUtils.payV2(this,userControl.getOrder().getUseMoney());
        }
        else if(weixin.isChecked()){
            //应该调用微信支付接口
            Log.i(TAG, "pay: 使用微信支付================");
            post(TYPE_USEOTHER);
        }
    }
    //未支付金额
    private int UnPayMoney = 0;
    //支付方式
    private int TYPE_USEMON = 1;
    private int TYPE_USEOTHER = 2;

    /**
     * 支付订单
     * @param type
     */
    private void post(int type){
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
        String url = (type == 1 ) ? url_fukuan : url_isQianfei;
        RequestParams result_params = new RequestParams(url);
        result_params.addParameter("userid",userControl.getUser().getUserid());
        result_params.addParameter("userMoney",userControl.getOrder().getUseMoney());
        result_params.addParameter("carId",userControl.getOrder().getCarId());
        Log.i(TAG, "post: -------------\n   "+result_params.toString());
        x.http().post(result_params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //使用第三方支付
                if(result.equals("ok")){
                    success();
                    return;
                }
                Log.i(TAG, "onSuccess: result ===== \n"+result);
                try {
                    UnPayMoney = Integer.parseInt(result);
                    if(UnPayMoney >=0)
                        success();
                    else
                        payNotFinish();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    uiUtils.showToast("支付失败");
                }
                dialogControl.cancel();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                uiUtils.showToast("失败");
                Log.i(TAG, "onError: ---------------------------");
                ex.printStackTrace();
                dialogControl.cancel();
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
     * 余额不足情况 提示用户充值或使用第三方支付
     */
    private void payNotFinish() {
        uiUtils.showToast("余额不足，请使用第三方支付或充值");
        int laslUseMoney =  userControl.getOrder().getUseMoney();
        userControl.getOrder().setUseMoney(-UnPayMoney);
        useMoney.setText("已经支付:"+(laslUseMoney+UnPayMoney)+"\n还需支付金额 ："+ (-UnPayMoney)+" 元");
        setpayViewVisible();
    }

    /**
     * 支付宝支付成功回调接口
     */
    private class myPaySuccess implements PaySuccess {

        @Override
        public void onSccuess() {
            RequestParams result_params = new RequestParams(url_isQianfei);
            result_params.addParameter("userid",userControl.getUser().getUserid());
            result_params.addParameter("usermoney",userControl.getOrder().getUseMoney());
            Log.i(TAG, "post: "+result_params.toString());
            x.http().post(result_params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.i(TAG, "onSuccess: result "+result);

                    dialogControl.cancel();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i("msp", ">>>>>>>>>>>>>>>>>>>>>>>>>>o2:"+ex.getMessage());
                    dialogControl.cancel();
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

    /**
     * 付款成功
     */
    private void success() {
        Log.i(TAG, "success: 支付成功  "+ UnPayMoney);
        UiUtils.showToast("支付成功");
        pay_or_not.setText("已付款");
        User user = userControl.getUser();
        user.setUsermoney(UnPayMoney < 0? 0:UnPayMoney);
        user.setUserstate(0);
        userControl.setUser(user);
        dialogControl.cancel();
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try{
                        Thread.sleep(3000);
                    }catch(Exception e){
                            e.printStackTrace();
                        }
                finish();
            }
        }).start();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_button:
                pay();
                break;
        }
    }
}