package com.joshua.a51bike.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.joshua.a51bike.application.BaseApplication;

import java.util.List;


/**
 * ============================================================
 * <p>
 * 版 权 ： 吴奇俊  (c) 2016
 * <p>
 * 作 者 : 吴奇俊
 * <p>
 * 版 本 ： 1.0
 * <p>
 * 创建日期 ： 2016/11/1 18:31
 * <p>
 * 描 述 ：
 * <p>
 * ============================================================
 **/

public class UiUtils {
    public static Boolean backstage = true;
    public static Toast toast ;
    public UiUtils(Context context){
        toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
    }
    /**
     * 获取到字符数组
     * @param tabNames  字符数组的id
     */
    public static String[] getStringArray(int tabNames) {
        return getResource().getStringArray(tabNames);
    }

    public static Resources getResource() {
        return BaseApplication.getApplication().getResources();
    }
    public static Context getContext(){
        return BaseApplication.getApplication();
    }
    /** dip转换px */
    public static int dip2px(int dip) {
        final float scale = getResource().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /** px转换dip */

    public static int px2dip(int px) {
        final float scale = getResource().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
    /**
     * 把Runnable 方法提交到主线程运行
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        // 在主线程运行
        if(android.os.Process.myTid()==BaseApplication.getMainTid()){
            runnable.run();
        }else{
            //获取handler
            BaseApplication.getHandler().post(runnable);
        }
    }

    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    public static Drawable getDrawalbe(int id) {
        return getResource().getDrawable(id);
    }

    public static int getDimens(int homePictureHeight) {
        return (int) getResource().getDimension(homePictureHeight);
    }
    /**
     * 延迟执行 任务
     * @param run   任务
     * @param time  延迟的时间
     */
    public static void postDelayed(Runnable run, int time) {
        BaseApplication.getHandler().postDelayed(run, time); // 调用Runable里面的run方法
    }
    /**
     * 取消任务
     * @param auToRunTask
     */
    public static void cancel(Runnable auToRunTask) {
        BaseApplication.getHandler().removeCallbacks(auToRunTask);
    }

    public static void showToast(String content){
        toast.setText(content);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setGravity(View.TEXT_ALIGNMENT_CENTER,0,0);
        toast.show();
    }

    public static void setBackstage(Boolean b){
        backstage = b;
    }
    public static Boolean getBackstage(){
        return backstage;
    }
    public static String getTopActivityInfo(Context context) {
        ActivityManager manager = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE));
        String topActivityName = "";
        if (Build.VERSION.SDK_INT >= 21) {
            List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
            ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
            if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                topActivityName = topAppProcess.processName;
//                info.topActivityName = "";
            }
        } else {
            //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
            List localList = manager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localList.get(0);
            topActivityName = localRunningTaskInfo.topActivity.getPackageName();
//            info.topActivityName = localRunningTaskInfo.topActivity.getClassName();
        }
        Log.i("UiUtils","topActivityName is "+topActivityName);
        return topActivityName;
    }
}
