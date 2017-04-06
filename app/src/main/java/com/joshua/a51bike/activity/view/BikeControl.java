package com.joshua.a51bike.activity.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joshua.a51bike.Interface.DialogCallBack;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.CurrencyAlerDialog;
import com.joshua.a51bike.activity.dialog.LocateProgress;
import com.joshua.a51bike.activity.dialog.OutControlDialog;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.bluetooth.BlueToothManager;
import com.joshua.a51bike.entity.Car;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by wangqiang on 2017/1/9.
 */
@ContentView(R.layout.bike_control)
public class BikeControl extends BaseActivity {
    private String TAG = "BikeControl";
    private String rbUrl = AppUtil.BaseUrl+"/user/huanche";
    private View rechange ,reback,start,lock;
    private final int POST_TIMER = 0x1;
    private final int CANCEL_LOCK_DIALOG = 0x2;
    private long time = 0L;
    private long beforTime= 0L;
    private BlueToothManager manager;
    private boolean canConnect = false;
    @ViewInject(R.id.bike_control_bid)
    private TextView bid;

    @ViewInject(R.id.bike_control_Route)
    private TextView Rout;

    @ViewInject(R.id.bike_control_timer)
    private TextView textTimer;

    private boolean isStart = false;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case POST_TIMER:
                    upTimerView();
                    break;
                case CANCEL_LOCK_DIALOG:
                    dialogControl.cancel();
                    uiUtils.showToast("锁车成功");
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Car car = new Car();
        car.setCarState(Car.STATE_START);
        carControl.setCar(car);
        manager = new BlueToothManager(BikeControl.this);
        //检查设备是否支持蓝牙
       if(manager.checkPhoneState()){
           Log.i(TAG, "onCreate: connect_BLE");
           manager.connect_ble();//连接设备
       }
       else{
            uiUtils.showToast("当前手机不支持蓝牙");
           finish();
       }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        manager.checkOpenBLE();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注销广播
        manager.unRegisterRecevier();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放蓝牙资源service
        manager.UnbindService();
    }
    private static final int REQUEST_ENABLE_BT = 0x01;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } else {
            Toast.makeText(this, "成功打开蓝牙", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        initBikeMes();
        findid();
        setLister();
    }

    private void initBikeMes() {
        Intent intent = getIntent();
        bid.setText("车牌号："+intent.getStringExtra("bid"));
        Rout.setText("剩余里程：15公里");
    }

    private void findid() {
        rechange = findViewById(R.id.bike_control_recharge);
        reback = findViewById(R.id.bike_control_reback);
        start = findViewById(R.id.bike_control_unlock);
        lock = findViewById(R.id.bike_control_lock);

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
                dialogControl.setDialog(new OutControlDialog(this,
                        "提示",
                        "确定退出当前控制界面？"));
                dialogControl.show();
                break;
            case R.id.bike_control_recharge:
              userControl.reCharge(BikeControl.this);
                break;
            case R.id.bike_control_reback:
                reback();
                break;
            case R.id.bike_control_lock:
                lockBike();
                break;
            case  R.id.bike_control_unlock:
                startBike();
                break;
            default:
                break;
        }
    }

    private void startBike() {
        if(carControl.getCar().getCarState() == 0){
            uiUtils.showToast("当前没有车辆可以控制哦！");
            return ;
        }
        if(isStart) return;
        isStart = true;
        manager.startBike();
        carControl.getCar().setCarState(Car.STATE_START);
        uiUtils.showToast("租车成功，开始计时");
        startTimer();
    }

    private void lockBike() {
        if(carControl.getCar().getCarState() == Car.STATE_AVALIABLE){
            uiUtils.showToast("当前没有车辆可以控制哦！");
            return ;
        }
        if(carControl.getCar().getCarState() == Car.STATE_PARKING)
            return;
        beforTime = time;
        manager.lockBike();
        dialogControl.setDialog(new LocateProgress(this,"正在锁车"));
        dialogControl.show();
        carControl.getCar().setCarState(Car.STATE_PARKING);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                    dialogControl.cancel();
                    handler.sendEmptyMessage(CANCEL_LOCK_DIALOG);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        stopTimer();
    }

    private long startTime = 0L;
    private Timer timer;
    public void startTimer(){
        startTime = System.currentTimeMillis();
        timer = new Timer();
        TimerTask task  = new TimerTask() {
            @Override
            public void run() {
                time = beforTime + System.currentTimeMillis() - startTime;
                handler.sendEmptyMessage(POST_TIMER);
            }
        };
        timer.schedule(task,0,1000);
    }
    public void stopTimer(){
        if(timer == null) return;
        timer.cancel();
        isStart = false;
    }
    public void upTimerView(){
        SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String d = format.format(time);
        textTimer.setText("使用时间 : "+d);
    }
    /*还车*/
    private void reback() {
        stopTimer();
        if(carControl.getCar().getCarState() == 0){
            uiUtils.showToast("当前没有车辆可以还哦！");
            return ;
        }
        dialogControl.setDialog(new CurrencyAlerDialog(this,
                "提示","请您将车辆停道指定位置并摆放整齐"
                ,new DialogCallBack(){
            @Override
            public void sure() {
                dialogControl.setDialog(new WaitProgress(BikeControl.this));
                dialogControl.show();
                realReback();
            }

            @Override
            public void cancel() {

            }
        }));
        dialogControl.show();
    }

    private void realReback() {
        long t = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time =  format.format(t);
        RequestParams params = new RequestParams(rbUrl);
        User user = userControl.getUser();
        params.addBodyParameter("userId",user.getUserid()+"");
        Car car = carControl.getCar();
        params.addBodyParameter("carId",car.getCarId()+"");
        post(params);
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
                    manager.returnBike();//还车
                    uiUtils.showToast("还车成功！");
                    carControl.getCar().setCarState(Car.STATE_AVALIABLE);
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