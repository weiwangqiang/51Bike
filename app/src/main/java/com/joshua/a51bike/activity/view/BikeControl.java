package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;

import org.xutils.view.annotation.ContentView;

/**
 * Created by wangqiang on 2017/1/9.
 */
@ContentView(R.layout.bike_control)
public class BikeControl extends BaseActivity {
    private UserControl userControl;
    private String TAG = "BikeControl";
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
                finish();
                break;
            case R.id.bike_control_recharge:
              userControl.reCharge(BikeControl.this);
                break;
            case R.id.bike_control_return:
                userControl.returnBike(BikeControl.this);
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
}