package com.joshua.a51bike.bluetooth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.joshua.a51bike.Interface.BleCallBack;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.CarControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.bluetooth.utils.Protocol;
import com.joshua.a51bike.entity.Car;
import com.joshua.a51bike.util.UiUtils;

import org.xutils.view.annotation.ContentView;

import static com.joshua.a51bike.bluetooth.BleManager.BIKE_ERROR;
import static com.joshua.a51bike.bluetooth.BleManager.BIKE_START;
import static com.joshua.a51bike.bluetooth.BleManager.BIKE_STOP;

@ContentView(R.layout.activity_test)
public class TestActivity extends BaseActivity {
    BleManager mBleManager;
    private final static String TAG = "TestActivity";
    private int mCurrentState;//设备当前状态
    private int mLastState;//设备上一次的状态
    private static final int STATE_START = 0x20;//设备开启
    private static final int STATE_STOP = 0x22;//设备上锁
    private static final int STATE_BACK = 0x23;//设备还车

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Car car = new Car();
        car.setCarMac("E899B6C8A9B9000000001036");
        CarControl.getCarControl().setCar(car);
        //初始化Ble管理器
        initBleManager();
        initState();
    }

    private void initState() {
        mCurrentState = STATE_BACK;
        mLastState = STATE_BACK;
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
                    case BIKE_STOP:
                        if (mLastState == STATE_START) {
                            mCurrentState = STATE_STOP;
                            mLastState = STATE_STOP;
                            doStopBike();
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
        startTimer();
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
            if (!mBleManager.connect()) {
                UiUtils.showToast("启动失败");
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
            UiUtils.showToast("还车成功");
            mLastState=STATE_BACK;
            mCurrentState=STATE_BACK;
            mBleManager.disconnect();
            postServerReturnBike();
        }

    }

    /**
     * 通知服务器还车
     */
    private void postServerReturnBike() {

    }

    /**
     * 通知服务器租车
     */
    private void postServerStartBike() {

    }

    @Override
    public void onClick(View v) {

    }

    private void startTimer() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleManager.disconnect();
    }
}
