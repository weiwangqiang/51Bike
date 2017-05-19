package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.MainActivity;
import com.joshua.a51bike.activity.control.LoginState;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.adapter.TimestampTypeAdapter;
import com.joshua.a51bike.application.BaseApplication;
import com.joshua.a51bike.entity.Car;
import com.joshua.a51bike.entity.Order;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.entity.UserAndUse;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.JsonUtil;
import com.joshua.a51bike.util.PrefUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import java.sql.Timestamp;

/**
 * class description here
 *
 *  启始 界面
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-11
 *
 */
@ContentView(R.layout.welcome)
public class WelCome extends BaseActivity {
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
        if(isFirst()){
            toFirstView();
            return ;
        }
        getUserMes();
    }
    private String url = AppUtil.BaseUrl +"/user/login";

    private void getUserMes() {
        String name = PrefUtils.getString(BaseApplication.getApplication(),PrefUtils.USER_NAME,"");
        if(name.length() == 0)
        {
            Log.i(TAG, "getUserMes: length is 0 ");
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
            return;
        }
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("username",name);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseUserMes(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
//                UiUtils.showToast("获取用户信息失败！");
                ToMainActivity();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                ToMainActivity();

            }

            @Override
            public void onFinished() {
                ToMainActivity();

            }
        });
    }
    //登陆成功
    private void parseUserMes(String result) {
        User user = JsonUtil.getUserObject(result);
        if(user != null){
            userControl.setUser(user);
            userControl.setUserState(new LoginState());
            toRent();
        }else{
            ToMainActivity();
        }

    }
    /**
     * 判断用户是否处于租车状态
     */
    private void toRent() {
        if(userControl.getUser() != null){
            //用户上次没有还车或者没有付款
            if ( userControl.getUser().getUserstate() == 2)
                  getLastOreder();
            else
                ToMainActivity();
        }
        else
            ToMainActivity();
    }
    private String url_getCurrent = AppUtil.BaseUrl+"/user/getCurrent";

    /**
     * 获取上次没有付款的UserAndUse
     */
    private void getLastOreder() {
        RequestParams result_params = new RequestParams(url_getCurrent);
        result_params.addParameter("userId",userControl.getUser().getUserid());
        x.http().get(result_params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseLastOrderResult(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                dialogControl.cancel();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {
                dialogControl.cancel();

            }
        });
    }

    /**
     * 解析结果
     * @param result
     */
    private Order order = new Order();
    private void parseLastOrderResult(String result) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
            gsonBuilder.registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter());
            Gson gson = gsonBuilder.create();
            UserAndUse userAndUse = gson.fromJson(result, UserAndUse.class);

            if (userAndUse != null) {
                userControl.setUserAndUse(userAndUse);
                //正在租车，先获取car的信息 然后跳转到控制界面
                if(userAndUse.getCarState() == Car.STATE_START){
                    order.setUseStartTime(userAndUse.getUseStartTime().getTime());
                    order.setCarId(userAndUse.getCarId());
                    userControl.setOrder(order);
                    getCarMes();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取车辆信息
     */
    private String url_getCarById  = AppUtil.BaseUrl+"/car/getCarById";
    public void getCarMes(){
        RequestParams result_params = new RequestParams(url_getCarById);
        result_params.addParameter("carId",userControl.getUserAndUse().getCarId());
        x.http().get(result_params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Car car = JsonUtil.getCarObject(result);
                if(car != null){
                    carControl.setCar(car);
                    Intent intent =  new Intent(WelCome.this, BikeControlActivity.class);
                    intent.putExtra("from_where","Exception");
                    startActivity(intent);
                }
                dialogControl.cancel();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                dialogControl.cancel();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {
                dialogControl.cancel();

            }
        });
    }
    public boolean isFirst(){
        Log.i(TAG, "isFirst: ");
        return PrefUtils.getBoolean(this,"isFirst",true);
    }
    public void toFirstView(){
        startActivity(new Intent(this,FirstClick.class));
        finish();
//        PrefUtils.setBoolean(this,"isFirst",false);
    }
    private boolean isSkip = false;
    //跳转到主界面
    public void ToMainActivity(){
        if(isSkip)
            return ;
        isSkip = true;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void finish() {
        super.finish();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}