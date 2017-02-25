package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.OutControlDialog;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import java.text.SimpleDateFormat;

/**
 * Created by wangqiang on 2017/1/9.
 */
@ContentView(R.layout.bike_control)
public class BikeControl extends BaseActivity {
    private UserControl userControl;
    private String TAG = "BikeControl";
    private String rbUrl = AppUtil.BaseUrl+"/user/huanche";
    private Button rechange ,reback,start,lock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        userControl = UserControl.getUserControl();
        findid();

        setLister();
    }

    private void findid() {
        rechange = (Button) findViewById(R.id.bike_control_recharge);
        reback = (Button) findViewById(R.id.bike_control_return);

        start = (Button) findViewById(R.id.bike_control_start);
        lock = (Button) findViewById(R.id.bike_control_lock);

    }

    private void setLister() {
        rechange.setOnClickListener(this);
        lock.setOnClickListener(this);
        reback.setOnClickListener(this);
        start.setOnClickListener(this);
        findViewById(R.id.left_back).setOnClickListener(this);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e(TAG,"--->>onWindowFocusChanged");
//        ViewGroup.LayoutParams params = rechange.getLayoutParams();
//        params.height = params.width;
//        rechange.setLayoutParams(params);
//        lock.setLayoutParams(params);
//        start.setLayoutParams(params);
//        reback.setLayoutParams(params);
//        Log.e(TAG,"--->>height is "+params.height+" width is "+params.width);

    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
//                finish();
                dialogControl.setDialog(new OutControlDialog(this,
                        "提示",
                        "确定退出当前控制界面？"));
                dialogControl.show();
                break;
            case R.id.bike_control_recharge:
              userControl.reCharge(BikeControl.this);
                break;
            case R.id.bike_control_return:
                reback();
                break;
            case R.id.bike_control_lock:
                userControl.lockBike(BikeControl.this);
                break;
            case  R.id.bike_control_start:
                userControl.startBike(BikeControl.this);
                break;
            default:
                break;
        }
    }
    /*还车*/
    private void reback() {
        long t = System.currentTimeMillis();
//        Timestamp    timestamp = new Timestamp(t);
//        Log.i(TAG, "reback: timestamp is "+timestamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time =  format.format(t);
//        long time = System.currentTimeMillis();
        RequestParams params = new RequestParams(rbUrl);
        User user = userControl.getUser();
        params.addBodyParameter("userId",user.getUserid()+"");
        params.addHeader("Content-Type","application/json");

        params.addBodyParameter("carId","1");
        params.addBodyParameter("useHour",time+"");
        params.addBodyParameter("useMoney","20");
        params.addBodyParameter("useEndTime",time+"");
        params.addBodyParameter("useStartTime",time+"");
        params.addBodyParameter("useTime",time+"");
        Log.i(TAG, "reback: params is "+params.toString());
        post(params);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
    }
    /*发送请求*/
    private void post(RequestParams params){
        Log.d(TAG, "post: post by xutils------>>");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result is "+result);
                dialogControl.cancel();

                if(result.equals("ok")){
                    uiUtils.showToast("还车成功！");
                    userControl.returnBike(BikeControl.this);
                }else
                    uiUtils.showToast("还车失败！");

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//              1handler.sendEmptyMessage(NET_ERROR);
                Log.e(TAG, "onError: onError", null);
                ex.printStackTrace();
                uiUtils.showToast("还车失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: cancel", null);
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
    }
    @Override
    public void onBackPressed() {
        dialogControl.setDialog(new OutControlDialog(BikeControl.this,
                "提示",
                "确定退出当前控制界面？"));
        dialogControl.show();

    }
}