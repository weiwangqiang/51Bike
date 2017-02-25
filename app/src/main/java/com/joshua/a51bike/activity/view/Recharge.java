package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.util.AppUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import java.text.SimpleDateFormat;

/**
 * Created by wangqiang on 2017/1/9.
 */
@ContentView(R.layout.recharge)
public class Recharge extends BaseActivity {
    private String TAG = "Recharge";
    private String url = AppUtil.BaseUrl + "/user/insertCharge";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    private void init() {
        setLister();
    }

    private void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);
        findViewById(R.id.reChange).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
                break;
            case R.id.reChange:
                reChange();
                break;
        }
    }
    private void reChange(){

        RequestParams params = new RequestParams(url);
        long t = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time =  format.format(t);
//        Time time = new Time(t);

        Log.d(TAG, "Login: time is "+6);
        params.addBodyParameter("userId",userControl.getUser().getUserid()+"");
        params.addBodyParameter("userCharge","100");
//        params.addBodyParameter("userTime",""+t);
        post(params);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
    }

    private void post(RequestParams params){
        Log.d(TAG, "post: post by xutils------>>");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "login come from Manager response is " + result.toString());

                if(result.equals("ok")){
                    uiUtils.showToast("充值成功！");
                }else
                    uiUtils.showToast("充值失败！");
                dialogControl.cancel();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//              handler.sendEmptyMessage(NET_ERROR);
                Log.e(TAG, "onError: onError", null);
                uiUtils.showToast("充值失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: cancel", null);
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
    }
}