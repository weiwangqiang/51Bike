package com.joshua.a51bike.activity.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.joshua.a51bike.R;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-11
 */
//@ContentView(R.layout.activity_main)
public class WelCome extends Activity {
    private String TAG = "WelCome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView((R.layout.welcome));
        init();
        Log.e(TAG," WelCome is created");
    }

    public void init() {
          findId();
        setLister();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3000);
                    ToMainActivity();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //跳转到主界面
    public void ToMainActivity(){
            finish();

    }
    public void findId() {

    }

    public void setLister() {
//        findViewById(R.id.left_back).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"------>> onResume<<<<<-------------");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"------>> onStart<<<<<-------------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"------>> onStop<<<<<-------------");

    }

    @Override
    public void finish() {
        super.finish();
        Log.e(TAG,"------>> onFinish <<<<<-------------");
    }
}