package com.joshua.a51bike.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RouteSearch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.DialogControl;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseMap;
import com.joshua.a51bike.activity.dialog.GPSAlerDialog;
import com.joshua.a51bike.activity.dialog.LocateProgress;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.activity.presenter.locatePresenter;
import com.joshua.a51bike.activity.view.BikeControlActivity;
import com.joshua.a51bike.activity.view.LeftMain;
import com.joshua.a51bike.activity.view.Pay;
import com.joshua.a51bike.activity.view.Use_Explain;
import com.joshua.a51bike.activity.view.searchBike;
import com.joshua.a51bike.adapter.TimestampTypeAdapter;
import com.joshua.a51bike.entity.Car;
import com.joshua.a51bike.entity.Order;
import com.joshua.a51bike.entity.ReadData;
import com.joshua.a51bike.entity.UserAndUse;
import com.joshua.a51bike.util.AMapUtil;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.JsonUtil;
import com.joshua.a51bike.util.PrefUtils;
import com.joshua.a51bike.util.SensorEventHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.joshua.a51bike.R.id.rent;
import static com.joshua.a51bike.util.JsonUtil.gson;

/**
 *  应用的主界面 =_=  ~~_~~  
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseMap {
    public String TAG = "MainActivity";
    private Context mContext;
    private Button location;
    public LatLng LastPoint;
    //canGetPos 是否可以更新当前位置
    private Boolean canGetPos = true,canShow = false;
    private Marker mLocMarker;
    private SensorEventHelper mSensorHelper;//自旋转的定位指针
    private Circle mCircle;
    private  Toolbar toolbar ;
    private  MapView mapView;
    private  AMap aMap;

    @ViewInject(R.id.main_use_explain)
    private TextView explain;

    private static final int REQUEST_CODE_LOCATION = 0x0001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGranted();
        mapView = (MapView) findViewById(R.id.main_mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        if(savedInstanceState == null)
        canShow = true;
        initDrawer();
    }

    /**
     *     初 始化权限
     */
    private void initGranted() {
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(MainActivity.this, "Location Granted", Toast.LENGTH_SHORT)
//                            .show();
                } else {
//                    Toast.makeText(MainActivity.this, "Location Denied", Toast.LENGTH_SHORT)
//                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 初始化主界面的toolbar
     */
    private void initDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_reorder_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void init() {
        mContext = this.getApplicationContext();
        userControl = UserControl.getUserControl();
        dialogControl = DialogControl.getDialogControl();
        findid();
        initmap();
        registerListener();
    }
    /*初始化map*/
    private void initmap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            locatepresener = locatePresenter.Instance();
            setUpMap();
        }
        addCenterMarker();
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        mlocationClient = new AMapLocationClient(this);

    }
//************************ 中间的marker *******************************************
    /**
     * 记录中心点移动前后的位置
     */
    private LatLng lastPosition = null;
    private LatLng nowPosition = null ;
    private void addCenterMarker() {
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition postion) {
                //屏幕中心的Marker跳动
                nowPosition = postion.target;
                if(lastPosition != null)
                {
                    float x = Math.abs((float)(nowPosition.latitude - lastPosition.latitude));
                    float y = Math.abs((float)(nowPosition.longitude - lastPosition.longitude));
                    if(x > 0.005 || y > 0.005){
                        startJumpAnimation();
                    }
                }
                lastPosition = nowPosition;
            }
        });
    }

    Marker screenMarker = null;
    /**
     * 在屏幕中心添加一个Marker
     */
    private void addMarkerInScreenCenter() {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        screenMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f,0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(screenPosition.x,screenPosition.y);

    }
    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {
        if (screenMarker != null ) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point =  aMap.getProjection().toScreenLocation(latLng);
            point.y -=  uiUtils.dip2px(80);

            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if(input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f)*(1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();
        }
    }
    //*****************************************************************************************


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

    }

    public void findid() {
        location = (Button) findViewById(R.id.main_location);
    }

//*************** listener 相关的方法***********************************

    /**
     * 注册监听
     */
    public View rentView;
    private void registerListener() {
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);
        locatepresener.setlatLonPointLister(this);
        mRouteSearch = new RouteSearch(this);
        location.setOnClickListener(this);
        explain.setOnClickListener(this);
        rentView =  findViewById(R.id.rent);
        rentView.setOnClickListener(this);

        View v =  findViewById(R.id.search_view);
        //用户第一次进来 ，介绍使用方法
//        if(isFirst()){
//            // ShowTipsView
//            ShowTipsView showtips = new ShowTipsBuilder(this)
//                    .setTarget(v)
//                    .setDescription("这里可以通过搜索车牌号租车哦")
//                    .setDelay(1000)
//                    .setBackgroundAlpha(70)
//                    .setCloseButtonColor(Color.WHITE)
//                    .setCloseButtonTextColor(Color.WHITE)
//                    .setCallback(new ViewCallBack())
//                    .build();
//            showtips.show(this);
//        }
        if(isExplain())
            explain.setVisibility(View.GONE);
    }
    public boolean isFirst(){
        Log.i(TAG, "isFirst: ");
        return PrefUtils.getBoolean(this,"isFirst",true);
    }
    //用户有没有看过使用手册
    public boolean isExplain(){
        return PrefUtils.getBoolean(this,"isExplain",false);
    }

    /**
     * 第一次用户进来的引导界面
     */
//    public class ViewCallBack implements ShowTipsViewInterface {
//
//        @Override
//        public void gotItClicked() {
//            // ShowTipsView
//            ShowTipsView showtips = new ShowTipsBuilder(MainActivity.this)
//                    .setTarget(location)
//                    .setDescription("这里可以定位哦")
//                    .setDelay(0)
//                    .setBackgroundAlpha(70)
//                    .setCloseButtonColor(Color.WHITE)
//                    .setCloseButtonTextColor(Color.WHITE)
//                    .build();
//            showtips.show(MainActivity.this);
//            PrefUtils.setBoolean(MainActivity.this,"isFirst",false);
//        }
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_location:
                getLocation();
                break;
            case rent:
                toRent();
                break;
            case R.id.main_use_explain:
                startActivity(new Intent(this, Use_Explain.class));
                break;
            default:
                break;
        }
    }

    /**
     * 用户点击扫码按钮
     */
    private void toRent() {
        if(userControl.getUser() != null){
            //用户上次没有还车或者没有付款
            if ( userControl.getUser().getUserstate() == 2)
            {
                getLastOrder();
                return ;
            }
        }
      userControl.saoma(MainActivity.this);
    }
    private String url_getCurrent = AppUtil.BaseUrl+"/user/getCurrent";
    /**
     * 获取上次没有付款的UserAndUse
     */
    private void getLastOrder() {
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
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
     * 解析getLastOrder() 方法的结果
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
                //已经还车，但是没有付款
                else if (userAndUse.getCarState() == Car.STATE_AVALIABLE){
                     Order order = new Order();
                    order.setCarId(userAndUse.getCarId());
                    order.setUseMoney(userAndUse.getUseMoney());
                    order.setUseStartTime(userAndUse.getUseStartTime().getTime());
                    order.setUseEndTime(userAndUse.getUseEndTime().getTime());
                    userControl.setOrder(order);
                    startActivity(new Intent(MainActivity.this,Pay.class));

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 用户正在租车，先获取车辆信息，然后直接跳转到控制界面
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
                        Intent intent = new Intent(MainActivity.this, BikeControlActivity.class);
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

    /**
     * 获取所有可出租车辆信息
     */
    private String getMesList_url =AppUtil.BaseUrl+ "/car/getgpsList";
    public void getCarMesList(){
        RequestParams result_params = new RequestParams(getMesList_url);
        x.http().get(result_params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseResult(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    //解析结果
    private List<ReadData> readDataList;
    private  void parseResult(String result) {
        Type type = new TypeToken<ArrayList<ReadData>>() {}.getType();
        readDataList =gson.fromJson(result, type);
        addCarMarker(readDataList);
    }

    public LatLonPoint point1 = new LatLonPoint(32.1979265479926, 119.51321482658388);
    public LatLonPoint point2 = new LatLonPoint(32.19794016630354, 119.51738834381104);
    public LatLonPoint point3 = new LatLonPoint(32.20268375393801, 119.51433062553406);
    LatLonPoint current = new LatLonPoint(32.210541666666664,119.508058);
    private void addCarMarker(List<ReadData> readDataList) {
        for(ReadData r : readDataList){
            MarkerOptions markerOptions = new MarkerOptions()
//                       .icon(BitmapDescriptorFactory.fromView(view))
                    .title("车牌号:"+r.getCarBattery2())
//                    .snippet("可行驶里程: " + i + " 公里")
                    .position(AMapUtil.convertToLatLng(new LatLonPoint(r.getWei(),r.getJin())))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
            aMap.addMarker(markerOptions);
        }
    }
//*******************************************************************

    /**
     * 获取当前位置
     */
    private void getLocation() {
//        getCarMesList();
        showCurrentPosition();
        //获取最新地理位置
        canGetPos = true;
        if(mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
        }
        locatepresener.getcurrentLocation(mlocationClient);//开始定位
        dialogControl.setDialog(new LocateProgress(MainActivity.this, "正在搜索......"));
        dialogControl.show();
    }
//*************** menu相关的方法***********************************
    //引入menu布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // ShowTipsView

        return true;
    }
    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    //处理menu事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                toSearchActivty();
                return true;
            case android.R.id.home:
                startActivity(new Intent(this, LeftMain.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 跳转到搜索界面
     */
    private void toSearchActivty() {
      startActivity(new Intent(this,searchBike.class));
    }
//**************************************************
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (0 == resultCode) {
            switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
                case GPS_REQUESTCODE:
                    getLocation();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 双击退出
     */
    private long mPressedTime = 0;

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) {//比较两次按键时间差
            uiUtils.showToast("再按一次退出程序");
            mPressedTime = mNowTime;
        } else {//退出程序
            Intent intent = new Intent("com.joshua.exit");
            sendBroadcast(intent);
        }
    }
//********************* marker相关回调及其顶部信息显示*****************************

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub
        if (marker.isInfoWindowShown()) {
            {
                marker.hideInfoWindow();
            }
        } else
            marker.showInfoWindow();
        changeCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        marker.getPosition(), 17, 0, 0)), new myCancelableCallback());
        return false;
    }
//*********************************************************************************

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        aMap.animateCamera(update, 1000, callback);

    }
    private class myCancelableCallback implements AMap.CancelableCallback {

        @Override
        public void onFinish() {
            Log.i(TAG, "onFinish: ");
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "onCancel: ");
        }
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        showCurrentPosition();
        locatepresener.getcurrentLocation(mlocationClient);//开始定位
        SensorHelper();
        if(canShow){
            showDialog();
            canShow = false;
        }
        getCarMesList();
    }

    /**
     * 显示开启gps的dialog
     */
    private void showDialog() {
        dialogControl.setDialog(new GPSAlerDialog(MainActivity.this));
        dialogControl.show();
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mapView.onPause();
        canGetPos = true;
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    //***************** g更新ui  ********************************
    /*显示当前位置*/
    private void showCurrentPosition() {
        if(null != mStartPoint){
            LatLng mLatLng = new LatLng(mStartPoint.getLatitude(),mStartPoint.getLongitude());
            changeCamera(
                    CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            mLatLng, 15, 0, 0)), new myCancelableCallback());
        }
    }



    private void SensorHelper() {
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        } else {
            mSensorHelper = new SensorEventHelper(this);
            if (mSensorHelper != null) {
                mSensorHelper.registerSensorListener();
                if (mSensorHelper.getCurrentMarker() == null && mLocMarker != null) {
                    mSensorHelper.setCurrentMarker(mLocMarker);
                }
            }
        }
    }


//***********自定义的定位回调**************************************

    /**
     * 定位成功的回调
     *
     * @param
     */
    @Override
    public void getstartlatLonPoint(AMapLocation aMapLocation) {
        Boolean b = dialogControl.getDialog() instanceof LocateProgress;
        if(b)
           dialogControl.cancel();
//        避免重复定位
        if (canGetPos) {
            aMap.clear();
            getCarMesList();
            canGetPos = false;
            LastPoint = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            addCircle(LastPoint, aMapLocation.getAccuracy());//添加定位精度圆
            addMarker(LastPoint);//添加定位图标
            mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
            /*移动到当前位置  CameraPosition 第三参数是偏移经度的角度*/
            addMarkerInScreenCenter();
            changeCamera(
                    CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            LastPoint, 15, 0, 0)), new myCancelableCallback());

        }
        else if( null != LastPoint){
            mCircle.setCenter(LastPoint);
            mCircle.setRadius(aMapLocation.getAccuracy());
            mLocMarker.setPosition(LastPoint);
        }
    }



    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

    /**
     * 添加精度圆
     * @param latlng
     * @param radius
     */
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    /**
     * 添加maker
     * @param latlng
     */
    private void addMarker(LatLng latlng) {
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle("position");
    }
    /**
     * 根据给定的地理名称和查询城市，返回地理编码的结果列表
     *
     * @param point
     */

    @Override
    public void getEndlatLonPoint(LatLonPoint point) {
    }

    /**
     * 定位失败的回调
     */
    @Override
    public void Error() {
        uiUtils.showToast("定位失败，请重试");
        dialogControl.cancel();
    }

}
