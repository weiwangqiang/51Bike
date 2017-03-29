package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.DialogControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.LocateProgress;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.entity.Car;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AMapUtil;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.JsonUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;


/**
 * Created by wangqiang on 2017/1/9.
 */
@ContentView(R.layout.bike_message)
public class BikeMessage extends BaseActivity {
    private Button rent;
    private String TAG = "BikeMessage";
    private String rentUrl = AppUtil.BaseUrl +"/user/zuche";
    private String CarMesUrl = AppUtil.BaseUrl +"/car/getCarById";

    private static int GET_BIKEMESSAGE = 1;
    private static int RENT_BIKE = 2;
    private TextView price,account;
    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mapView.onCreate(savedInstanceState); // 此方法必须重写
    }

    private void init() {
        getUrl();
        findid();
        initmap();
        setLister();
        getCarMes();
    }

    private void getUrl() {
       Intent inetnt =  getIntent();
        String url =inetnt.getStringExtra("url");
        Log.i(TAG, "getUrl: "+url);

    }
    /*获取车辆信息*/
    private void getCarMes() {
        Log.d(TAG, "getCarMes: begin to getCarMes");
        RequestParams params = new RequestParams(CarMesUrl);
        params.addBodyParameter("carId","1");
        post(params,GET_BIKEMESSAGE);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
    }
    private void setLister() {
        rent.setOnClickListener(this);
        findViewById(R.id.left_back).setOnClickListener(this);
        findViewById(R.id.bike_mes_agreement).setOnClickListener(this);
    }

    private void findid() {
        rent = (Button) findViewById(R.id.bike_mes_rent);
        mapView = (MapView) findViewById(R.id.bike_mes_mapView);
        price = (TextView) findViewById(R.id.bike_mes_price);
        account = (TextView) findViewById(R.id.bike_mes_account);

    }
    private void initmap() {
        if (aMap == null) {
            Log.w(TAG,"------>> aMap is null");
            aMap = mapView.getMap();
        }
        aMap.clear();
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        dialogControl = DialogControl.getDialogControl();
        dialogControl.setDialog(new LocateProgress(BikeMessage.this,"正在定位"));
        dialogControl.show();
        addMarker();
    }

    private void addMarker() {
         LatLonPoint point1 = new LatLonPoint(32.1979265479926 ,119.51321482658388) ;
        MarkerOptions markerOptions = new MarkerOptions()
                .title("车牌号 : 2525")
                .snippet("可行驶里程 : "+20+" 公里")
                .position(AMapUtil.convertToLatLng(point1))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
        Marker marker = aMap.addMarker(markerOptions);
        marker.showInfoWindow();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        aMap.moveCamera(CameraUpdateFactory.
                changeLatLng(new LatLng(point1.getLatitude(),
                        point1.getLongitude())));

        dialogControl.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.bike_mes_rent:
                rent();
            break;
            case R.id.bike_mes_agreement:
                toWebView();
                break;
            default:
                break;
        }
    }

    public void toWebView(){
        Intent intent = new Intent(this,WebView.class);
        intent.putExtra("title","51get租车服务条款");
        intent.putExtra("url","51get租车服务条款");

        startActivity(intent);
    }
    private void rent(){

        RequestParams params = new RequestParams(rentUrl);
        User user = userControl.getUser();
        params.addBodyParameter("userid",user.getUserid()+"");
//        params.addBodyParameter("carState","0");

        params.addBodyParameter("carId","1");
        params.addBodyParameter("carName","one");
//        params.addBodyParameter("carPrice","20");
        Log.i(TAG, "rent: param "+params.toString());
        post(params,RENT_BIKE);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
    }
    /*上传数据
    * */
     private void post(final RequestParams params, final int kind){
        Log.d(TAG, "post: post by xutils------>> " +rentUrl);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: king is "+kind+"result is "+result);
                if(kind==GET_BIKEMESSAGE){
                    parseCar(result);
                }else
                {
                    parseRent(result);
                }
                dialogControl.cancel();
                Log.i(TAG, "onSuccess: cancel");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//              handler.sendEmptyMessage(NET_ERROR);
                ex.printStackTrace();
                Log.e(TAG, "onError: onError", null);
                uiUtils.showToast("失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: cancel", null);
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
    }
    private void parseCar(String result) {
        Car car = JsonUtil.getCarObject(result);
        Log.i(TAG, "parseCar: car is "+car.getCarName());
        if(null != car){
            carControl.setCar(car);
            Car car1 = carControl.getCar();
            upCarView();
        }
    }
    /*更新车辆信息到UI*/
    private void upCarView() {
//        uiUtils.showToast("获取信息成功! ");
        Toast.makeText(BikeMessage.this,"获取信息成功!",Toast.LENGTH_SHORT).show();
        Log.i(TAG, "upCarView: upCarView");
        price.setText(carControl.getCar().getCarPrice()+"元/小时");
        account.setText(userControl.getUser().getUsermoney()+"元");
    }

    private void parseRent(String result) {
        Log.i(TAG, "parseRent: "+result);
        if(result.equals("ok")){
            uiUtils.showToast("租车成功！");
            userControl.rent(BikeMessage.this);
        }else
            uiUtils.showToast("租车失败！");
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
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
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}