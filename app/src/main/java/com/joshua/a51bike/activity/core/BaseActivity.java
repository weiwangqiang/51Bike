package com.joshua.a51bike.activity.core;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.joshua.a51bike.activity.control.CarControl;
import com.joshua.a51bike.activity.control.DialogControl;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.receiver.ExitReceiver;
import com.joshua.a51bike.util.UiUtils;

import org.xutils.x;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    protected Activity mBaseActivity;
    private ExitReceiver exitReceiver;
    public static String EXIT_APP_ACTION = "com.joshua.exit";
    private Boolean isFinished = false;
    public UiUtils uiUtils;
    public DialogControl dialogControl;
    public CarControl carControl;
    public UserControl userControl;
    public final int NET_SUCCESS = 0x1;
    public final int NET_ERROR = 0x2;
    private String TAG = "BaseActivity";
    public BaseActivity(){
        mBaseActivity=this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiUtils = new UiUtils(getApplicationContext());
        dialogControl = DialogControl.getDialogControl();
        carControl = CarControl.getCarControl();
        userControl = UserControl.getUserControl();
        initScreen();
        initXUtils();
        initReceiver();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Boolean b = UiUtils.getBackstage();
//        if(b){
//            UiUtils.setBackstage(false);
//            Log.e(TAG,"------>> is  backStage and start welcome <<<<<-------------");
//            Intent intent = new Intent(BaseActivity.this, WelCome.class);
//            startActivity(intent);
//        }
    }
    @Override
    protected void onStop() {
        super.onStop();

//        if (!isFinished) {
//            //括号内部的代码请单独提成一个方法  我这里是为了视觉 懒了
//            String packageName="com.joshua.a51bike";//我们自己的应用的包名
//            String topActivityClassName= UiUtils.getTopActivityInfo(this);
//            if (packageName!=null&&topActivityClassName!=null && !topActivityClassName.startsWith(packageName))
//            {
//                //app已经后台
//                UiUtils.setBackstage(true);
//                Log.e(TAG,"------>> is to backStage<<<<<-------------");
//            }
//        }
    }
    @Override
    public void finish() {
        super.finish();
        isFinished = true;
    }

    /**
     * 初始化屏幕方向
     */
    private void initScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏
    }

    /**
     * 初始化xUtils
     */
    private void initXUtils() {
        x.view().inject(this);
        x.Ext.init(getApplication());
    }

    /**
     * 初始化退出广播监听
     */
    private void initReceiver() {
        exitReceiver = new ExitReceiver();
        registerExitReceiver();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterExitReceiver();
    }

    /**
     * 注册退出广播
     */
    private void registerExitReceiver() {
        IntentFilter exitFilter = new IntentFilter();
        exitFilter.addAction(EXIT_APP_ACTION);
        registerReceiver(exitReceiver, exitFilter);
    }

    /**
     * 注销退出广播
     */
    private void unRegisterExitReceiver() {
        unregisterReceiver(exitReceiver);
    }


}
