package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joshua.a51bike.Interface.BleCallBack;
import com.joshua.a51bike.Interface.DialogCallBack;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.OpenBLEAlerDialog;
import com.joshua.a51bike.activity.dialog.OutControlDialog;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.adapter.TimestampTypeAdapter;
import com.joshua.a51bike.bluetooth.BleManager;
import com.joshua.a51bike.entity.Car;
import com.joshua.a51bike.entity.Order;
import com.joshua.a51bike.entity.Preference;
import com.joshua.a51bike.entity.ReadData;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.JsonUtil;
import com.joshua.a51bike.util.LocateManager;
import com.joshua.a51bike.util.PrefUtils;
import com.joshua.a51bike.util.UiUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.sql.Timestamp;

import static com.joshua.a51bike.activity.control.CarControl.car;

/**
 * 车辆控制界面
 */
@ContentView(R.layout.bike_control)
public class BikeControlActivity extends BaseActivity {
    @ViewInject(R.id.bike_control_bid)//bike的id
    private TextView bid;
    @ViewInject(R.id.bike_control_ReleasePower)//显示剩余电量
    private TextView ReleasePower;

    @ViewInject(R.id.bike_control_bikeState)//显示当前车辆状态
    private TextView bikeState;

    private BleManager mBleManager;
    private final static String TAG = "BikeControlActivity";
    private int mCurrentState;//设备当前状态
    private int mLastState;//设备上一次的状态
    private static final int STATE_START = 0x20;//设备开启
    private static final int STATE_STOP = 0x22;//设备上锁
    private static final int STATE_BACK = 0x23;//设备还车

    private static final int REQUEST_GETGPS_MES = 0x01;//获取当前bike信息请求
    private static final int REQUEST_GETGPS_INSCHOOL = 0x02;//获取当前bike位置是否在本校请求
    private static final String BIKE_STATE_LOCK = "车辆状态 ：锁定";
    private static final String BIKE_STATE_START = "车辆状态 ：启动";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findid();
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initBikeMes();
        //初始化Ble管理器
        initBleManager();
        initState();
        getWhereCome();
    }

    private void findid() {
        findViewById(R.id.left_back).setOnClickListener(this);
        mapView = (MapView) findViewById(R.id.bike_control_MapView);
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 判断是否由于异常退出app后，再次重新进入控制界面
     */
    private  String from;
    private void getWhereCome() {
        Intent intent = getIntent();
        from = intent.getStringExtra("from_where");
        if(from.equals("Exception")){
            //是异常退出，显=显示开启蓝牙的对话框
            dialogControl.setDialog(new
                    OpenBLEAlerDialog(this,
                    new DialogCallBack() {
                        @Override
                        public void sure() {
                            startBike(null);
                        }

                        @Override
                        public void cancel() {
                            startBike(null);
                        }
                    }));
            dialogControl.show();
        }
    }

    private void initBikeMes() {
        bid.setText("车牌号：" +carControl.getCar().getCarId()+"");
        //获取当前车辆位置电量详情
        getGPSMes(REQUEST_GETGPS_MES);
    }

    private void initState() {
        if(PrefUtils.getInt(mBaseActivity, Preference.USER_BIKE_STATE,STATE_BACK)==STATE_BACK){
            mCurrentState = STATE_BACK;
            mLastState = STATE_BACK;
        }else if(PrefUtils.getInt(mBaseActivity,Preference.USER_BIKE_STATE,STATE_BACK)==STATE_STOP){
            mCurrentState = STATE_STOP;
            mLastState = STATE_STOP;
        }
        else
            mLastState = STATE_START;

    }

    private void initBleManager() {
        mBleManager = new BleManager(mBaseActivity, new BleCallBack() {
            //Gatt连接回调
            @Override
            public void onGattConnect(String action) {
                if (BleManager.ACTION_GATT_CONNECTED.equals(action)) {
                    //蓝牙连接成功
                    mBleManager.startBike();
                } else if (BleManager.ACTION_GATT_DISCONNECTED
                        .equals(action)) {
                    mBleManager.getmBluetoothGatt().disconnect();
                    mBleManager.getmBluetoothGatt().close();
                    UiUtils.runOnUiThread(new ConnectErrorRun());
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

            //获取设备状态 开关机状态会回调这个方法
            @Override
            public void getStateFromDevice(int state) {
                switch (state) {
                    //设备处于启动状态
                    case BleManager.BIKE_START:
                        Log.i(TAG, "getStateFromDevice: 设备启动啦==========");
                        if (mLastState == STATE_BACK||mLastState == STATE_STOP) {
                            mCurrentState = STATE_START;
                            mLastState = STATE_START;
                           UiUtils.runOnUiThread(new doStartBikeRun());
                        }
                        break;
                    //设置处于上锁状态
                    case BleManager.BIKE_STOP:
                        if (mLastState == STATE_START) {
                            mCurrentState = STATE_STOP;
                            mLastState = STATE_STOP;
                            UiUtils.runOnUiThread(new doStopBikeRun());
                        }else if(mLastState==STATE_STOP||mLastState==STATE_BACK){
//                            UiUtils.showToast("连接失败，请重试");
                            mBleManager.disconnect();
                            dialogControl.cancel();
                        }
                        break;
                    case BleManager.BIKE_ERROR:
//                        UiUtils.showToast("连接失败，请重试");
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
        Log.i(TAG, "doStartBike: 通知服务器租车啦===============");
        dialogControl.cancel();
        int state=userControl.getUser().getUserstate();
        if(state==User.STATE_USEING){
        }else{
            if( !from.equals("Exception") )
                postServerStartBike();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();// 此方法必须重写
        //检查设备是否支持Ble
        if (!mBleManager.checkAndInit())
            UiUtils.showToast("启动失败");
        //检查设备是否打开Ble
        mBleManager.checkOpenBLE();
        upDataBikeState();
    }

    //蓝牙连接失败
    private class ConnectErrorRun implements Runnable{
        @Override
        public void run() {
            UiUtils.showToast("蓝牙连接失败");
            dialogControl.cancel();
        }
    }

    //停止的runnable
    private class doStopBikeRun implements Runnable{
        @Override
        public void run() {
            doStopBike();
            upDataBikeState();
        }
    }
    //启动的runnable
    private class doStartBikeRun implements Runnable{
        @Override
        public void run() {
            doStartBike();
            upDataBikeState();
        }
    }
    private void upDataBikeState() {
        if(mLastState == STATE_START)
            bikeState.setText(BIKE_STATE_START);
        else
            bikeState.setText(BIKE_STATE_LOCK);
    }

    public void reCharge(View view) {
        userControl.accountRecharge(BikeControlActivity.this);
    }
    public void startBike(View view) {
        //mBluetoothGatt 不为null说明之前有连接过，先把他断开
        if(  mBleManager.getmBluetoothGatt() != null){
            mBleManager.getmBluetoothGatt().disconnect();
            mBleManager.getmBluetoothGatt().close();
        }
        Log.i(TAG, "startBike: ----------------点击开启车辆-----------");
        Log.i(TAG, "startBike: mLastState is "+mLastState);
        Log.i(TAG, "startBike: STATE_BACK is "+STATE_BACK
                + " \n         STATE_STOP is "+ STATE_STOP
        +"\n state_start is :" + STATE_START );
            if (mLastState == STATE_BACK||mLastState == STATE_STOP) {
                dialogControl.setDialog(new WaitProgress(mBaseActivity));
                dialogControl.show();
                if (!mBleManager.connect()) {
                    UiUtils.showToast("启动失败");
                    dialogControl.cancel();
                }else
                    Log.i(TAG, "startBike: success =================== ");
            }else {
                UiUtils.showToast("请不要重复开启车辆");
            }
    }

    public void lockBike(View view) {
        if(mCurrentState == STATE_BACK){
            UiUtils.showToast("没有车辆可以控制哦~");
            return;
        }
        UiUtils.showToast("请直接长按按钮锁车");
    }
    private MapView  mapView;
    private LatLng center = new LatLng(32.19281953769914 ,119.52218413352966);

    public void returnBike(View view) {
        //判断是否停在江大内部
//        dialogControl.setDialog(new WaitProgress(mBaseActivity));
//        dialogControl.show();
//        getGPSMes(REQUEST_GETGPS_INSCHOOL);
        Log.i(TAG, "returnBike: mCurrentState is "+mCurrentState );
        if (mCurrentState == STATE_START) {
            UiUtils.showToast("请先长按按钮锁车");
        }else if(mCurrentState==STATE_STOP){
            dialogControl.setDialog(new WaitProgress(mBaseActivity));
            dialogControl.show();
            postServerReturnBike();
        }else if(mCurrentState == STATE_BACK)
            UiUtils.showToast("没有车辆可以还哦~");
    }
    /**
     * 通知服务器租车
     */
    private String rentUrl = AppUtil.BaseUrl +"/user/zuche";
    private void postServerStartBike() {
        Log.i(TAG, "postServerStartBike: -------------------开始租车啦----------------------");
        RequestParams params = new RequestParams(rentUrl);
        User user = userControl.getUser();
        params.addBodyParameter("userid",user.getUserid()+"");
        params.addBodyParameter("carId",carControl.getCar().getCarId()+"");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess:  is result is "+result);
                parseRent(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Log.e(TAG, "onError: onError", null);
//                UiUtils.showToast("失败！");
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
    private void parseRent(String result) {
        Log.i(TAG, "parseRent: "+result);
        if(result.equals("error1"))
            return;
        long time = Long.valueOf(result);
        //已经租车了，就返回
        if(time !=0L ){
            Order order = new Order();
            order.setUseStartTime(time);
            userControl.setOrder(order);
//            car.setCarMac(bike_mac);
            carControl.setCar(car);
            userControl.getUser().setUserstate(User.STATE_USEING);
            UiUtils.showToast("租车成功");
        }else
            uiUtils.showToast("租车失败！");
    }
    /**
     * 通知服务器还车
     */
    private String rbUrl = AppUtil.BaseUrl + "/user/huanche2";
    private void postServerReturnBike() {
        Log.i(TAG, "postServerReturnBike: ----------------");
        RequestParams params = new RequestParams(rbUrl);
        final User user = userControl.getUser();
        params.addBodyParameter("userId", user.getUserid() + "");
        Car car = carControl.getCar();
        params.addBodyParameter("carId", car.getCarId() + "");
        params.addBodyParameter("carPrice", car.getCarPrice() + "");
        params.addBodyParameter("startTime",userControl.getOrder().getUseStartTime() + "");
        params.addBodyParameter("useDistance", "4512");
        Log.i(TAG, "postServerReturnBike: ------------ "+params.toString());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result is ------------------ \n  " + result);
                dialogControl.cancel();
                try{
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
                    gsonBuilder.registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter());
                    Gson gson = gsonBuilder.create();
                    Order order = gson.fromJson(result,Order.class);
                    if (order != null) {
                        Log.i(TAG, "onSuccess: carid "+order.toString());
                        userControl.setOrder(order);
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

    /**
     * 获取当前车辆的位置电量等信息
     * @param
     */
    public void getGPSMes(final int type){
        String carId = carControl.getCar().getCarMac();
        String getGPS_URL= AppUtil.BaseUrl+"/car/getGps";
        RequestParams params = new RequestParams(getGPS_URL);
        Log.i(TAG, "getGPSMes: carid length : "+carId.length());
        params.addBodyParameter("id",carId);
        Log.i(TAG, "getGPSMes: "+params.toString());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess:  is result is "+result);
                ReadData readData = JsonUtil.getReadDataByString(result);
                if(type == REQUEST_GETGPS_MES){
                    upBikeGPSMes(readData);
                }
                else
                    checkInSchool(readData);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                UiUtils.showToast("获取车辆位置失败");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     *  更新车辆详情
     * @param readData
     */
    private void upBikeGPSMes(ReadData readData ) {
        ReleasePower.setText("剩余电量："+readData.getCarBattery2()+" % ");

    }
    //判断是否在学校，如果在本校则通知服务器还车
    public void checkInSchool(ReadData readData){
        dialogControl.cancel();

        LocateManager l = new LocateManager(mapView.getMap());
        Log.i(TAG, "checkInSchool: jin :"+readData.getJin()+"\n : wei "+readData.getWei());
        LatLng lat = new LatLng(readData.getWei(),readData.getJin());
        if(!l.isInSchool(lat)){
            UiUtils.showToast("请将车辆停在江苏大学内部");
//            return ;
        }

        if (mCurrentState == STATE_START) {
            UiUtils.showToast("请先长按按钮锁车");
        }else if(mCurrentState==STATE_STOP){
            dialogControl.setDialog(new WaitProgress(mBaseActivity));
            dialogControl.show();
            postServerReturnBike();
        }
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

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private static final int REQUEST_ENABLE_BT = 0x01;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (resultCode){
                case REQUEST_ENABLE_BT:
                    //设置蓝牙可配对模式

                    break;
            }
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
        mBleManager.disconnect();
        mapView.onDestroy();// 此方法必须重写
    }

    @Override
    protected void onStop() {
        super.onStop();

//        PrefUtils.setInt(mBaseActivity,Preference.USER_BIKE_STATE,mCurrentState);
    }
}
