package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

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

/**
 * Created by wangqiang on 2017/1/9.
 *
 *  用户使用车辆，还车后的支付界面
 *
 */
@ContentView(R.layout.pay)
public class Pay extends BaseActivity {
    private String TAG = "Pay";
    private String url = AppUtil.BaseUrl +"/user/updateMoney";
    private int money = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    public void init() {
        initActionBar();
        setLister();
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
        if(zhifubao.isChecked()){
            uiUtils.showToast("正在跳转");
            PayUtils payUtils = PayUtils.getPayUtils();
            payUtils.setPaySuccess(new myPaySuccess());
            payUtils.payV2(this,money);
        }
        else {
            post();
        }

    }

    private class myPaySuccess implements PaySuccess {

        @Override
        public void onSccuess() {
            post();
        }
    }

    private void success() {
        UiUtils.showToast("支付成功");
        User user = userControl.getUser();
        user.setUsermoney(user.getUsermoney()-money);
        userControl.setUser(user);
        finish();
    }

    private void post(){
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
        RequestParams result_params = new RequestParams(url);
        result_params.addParameter("userid",userControl.getUser().getUserid());
        result_params.addParameter("usermoney",userControl.getUser().getUsermoney() - money);
        Log.i(TAG, "post: "+result_params.toString());
        x.http().post(result_params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result "+result);
                success();
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