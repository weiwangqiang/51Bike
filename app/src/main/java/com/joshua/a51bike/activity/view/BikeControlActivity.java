package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joshua.a51bike.Interface.BleCallBack;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.OutControlDialog;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.bluetooth.BleManager;
import com.joshua.a51bike.entity.Car;
import com.joshua.a51bike.entity.Preference;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.PrefUtils;
import com.joshua.a51bike.util.UiUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 车辆控制界面
 */
@ContentView(R.layout.bike_control)
public class BikeControlActivity extends BaseActivity {
    @ViewInject(R.id.bike_control_bid)
    private TextView bid;
    @ViewInject(R.id.bike_control_Route)
    private TextView Rout;

    private BleManager mBleManager;
    private final static String TAG = "TestActivity";
    private int mCurrentState;//设备当前状态
    private int mLastState;//设备上一次的状态
    private static final int STATE_START = 0x20;//设备开启
    private static final int STATE_STOP = 0x22;//设备上锁
    private static final int STATE_BACK = 0x23;//设备还车



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        //初始化Ble管理器
        initBleManager();
        initState();
    }
    private void init() {
        initBikeMes();
        findId();
    }

    private void initBikeMes() {
        Intent intent = getIntent();
        bid.setText("车牌号：" + intent.getStringExtra("bid"));
        Rout.setText("剩余里程：15公里");
    }

    private void findId() {
        findViewById(R.id.left_back).setOnClickListener(this);
    }

    private void initState() {
        if(PrefUtils.getInt(mBaseActivity,Preference.USER_BIKE_STATE,STATE_BACK)==STATE_BACK){
            mCurrentState = STATE_BACK;
            mLastState = STATE_BACK;
        }else if(PrefUtils.getInt(mBaseActivity,Preference.USER_BIKE_STATE,STATE_BACK)==STATE_STOP){
            mCurrentState = STATE_STOP;
            mLastState = STATE_STOP;
        }

    }

    private void initBleManager() {
        mBleManager = new BleManager(mBaseActivity, new BleCallBack() {
            //Gatt连接回调
            @Override
            public void onGattConnect(String action) {
                if (BleManager.ACTION_GATT_CONNECTED.equals(action)) {
                    mBleManager.startBike();
                } else if (BleManager.ACTION_GATT_DISCONNECTED
                        .equals(action)) {
                    UiUtils.showToast("启动失败");
                }
            }

            //判断是否要继续向Ble设备写数据
            @Override
            public void onCharacteristicContinueWrite() {
                mBleManager.sendCommandToDevice(true);
            }
            //开始向Ble设备读取数据
            @Override
            public void onCharacteristicFinishWrite() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBleManager.readStateFromDevice();
            }

            //获取设备状态
            @Override
            public void getStateFromDevice(int state) {
                switch (state) {
                    case BleManager.BIKE_START:
                        if (mLastState == STATE_BACK||mLastState == STATE_STOP) {
                            mCurrentState = STATE_START;
                            mLastState = STATE_START;
                            doStartBike();
                        }
                        break;
                    case BleManager.BIKE_STOP:
                        if (mLastState == STATE_START) {
                            mCurrentState = STATE_STOP;
                            mLastState = STATE_STOP;
                            doStopBike();
                        }else if(mLastState==STATE_STOP||mLastState==STATE_BACK){
                            UiUtils.showToast("连接失败，请重试");
                            mBleManager.disconnect();
                            dialogControl.cancel();
                        }
                        break;
                    case BleManager.BIKE_ERROR:
                        UiUtils.showToast("连接失败，请重试");
                        mBleManager.disconnect();
                        break;
                }
            }
        });
    }
    /**
     * 进行停止电动车的一系列逻辑操作
     */
    private void doStopBike() {
        UiUtils.showToast("车已上锁");
    }

    /**
     * 进行开启电动车的一系列逻辑操作
     */
    private void doStartBike() {
        dialogControl.cancel();
        postServerStartBike();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //检查设备是否支持Ble
        if (!mBleManager.checkAndInit()) {
            UiUtils.showToast("启动失败");
        }
        //检查设备是否打开Ble
        mBleManager.checkOpenBLE();
    }


    public void startBike(View view) {
        if (mLastState == STATE_BACK||mLastState == STATE_STOP) {
            dialogControl.setDialog(new WaitProgress(mBaseActivity));
            dialogControl.show();
            if (!mBleManager.connect()) {
                UiUtils.showToast("启动失败");
                dialogControl.cancel();
            }
        }else {
            UiUtils.showToast("请不要重复开启车辆");
        }
    }

    public void lockBike(View view) {
        UiUtils.showToast("请直接长按按钮锁车");
    }

    public void returnBike(View view) {
        if (mCurrentState == STATE_START) {
            UiUtils.showToast("请先长按按钮锁车");
        }else if(mCurrentState==STATE_STOP){
            dialogControl.setDialog(new WaitProgress(mBaseActivity));
            dialogControl.show();
            postServerReturnBike();
        }

    }
    /**
     * 通知服务器租车
     */
    private String rentUrl = AppUtil.BaseUrl +"/user/zuche";
    private void postServerStartBike() {
        RequestParams params = new RequestParams(rentUrl);
        User user = userControl.getUser();
        params.addBodyParameter("userid",user.getUserid()+"");
        params.addBodyParameter("carId",5+"");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess:  is result is "+result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Log.e(TAG, "onError: onError", null);
                UiUtils.showToast("失败！");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: cancel", null);
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFinished() {

            }
        });
    }
    /**
     * 通知服务器还车
     */
    private String rbUrl = AppUtil.BaseUrl + "/user/huanche";
    private void postServerReturnBike() {
        RequestParams params = new RequestParams(rbUrl);
        User user = userControl.getUser();
        params.addBodyParameter("userId", user.getUserid() + "");
        Car car = carControl.getCar();
        params.addBodyParameter("carId", car.getCarId() + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result is " + result);
                dialogControl.cancel();
                try{
                    if (result.equals("ok")) {
                        UiUtils.showToast("还车成功！");
                        mLastState=STATE_BACK;
                        mCurrentState=STATE_BACK;
                        mBleManager.disconnect();
                        carControl.getCar().setCarState(Car.STATE_AVALIABLE);
                        userControl.returnBike(mBaseActivity);
                        dialogControl.cancel();
                    } else
                        UiUtils.showToast("还车失败！");
                    dialogControl.cancel();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: onError", null);
                ex.printStackTrace();
                UiUtils.showToast("还车失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: cancel", null);
                dialogControl.cancel();
            }

            @Override
            public void onFinished() {
                dialogControl.cancel();
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                dialogControl.setDialog(new OutControlDialog(this,
                        "提示",
                        "确定退出当前控制界面？"));
                dialogControl.show();
                break;
            default:
                break;
        }
    }
    @Override
    public void onBackPressed() {
        dialogControl.setDialog(new OutControlDialog(mBaseActivity,
                "提示",
                "确定退出当前控制界面？"));
        dialogControl.show();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: -------------------- ");
        mBleManager.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PrefUtils.setInt(mBaseActivity,Preference.USER_BIKE_STATE,mCurrentState);
    }
}
