package com.joshua.a51bike.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
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
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.DialogControl;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseMap;
import com.joshua.a51bike.activity.dialog.GPSAlerDialog;
import com.joshua.a51bike.activity.dialog.LocateProgress;
import com.joshua.a51bike.activity.dialog.MarginAlerDialog;
import com.joshua.a51bike.activity.presenter.locatePresenter;
import com.joshua.a51bike.activity.presenter.mapPresenter;
import com.joshua.a51bike.activity.view.CircleImageView;
import com.joshua.a51bike.util.AMapUtil;
import com.joshua.a51bike.util.SensorEventHelper;

import org.xutils.view.annotation.ContentView;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseMap implements LocationSource {
    public String TAG = "MainActivity";
    private CircleImageView userIcn;
    private TextView userName,userMoney,userCash;
    private DrawerLayout drawer;
    private Context mContext;
    private Button location;
    public LatLng LastPoint;
    private double x;
    private double y;
    private Boolean isFirst = true;

    private Marker mLocMarker;
    private SensorEventHelper mSensorHelper;
    private OnLocationChangedListener mListener;//定位监听
    private Circle mCircle;

    public LatLonPoint point1 = new LatLonPoint(32.1979265479926, 119.51321482658388);
    public LatLonPoint point2 = new LatLonPoint(32.19794016630354, 119.51738834381104);
    public LatLonPoint point3 = new LatLonPoint(32.20268375393801, 119.51433062553406);
    public LatLonPoint[] points = new LatLonPoint[]{point1, point2, point3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         init();
        if(savedInstanceState == null)
        {
            dialogControl.setDialog(new GPSAlerDialog(MainActivity.this));
            dialogControl.show();
        }
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initDrawer();
    }

    private void initDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initLeftMain();
        //实现左右滑动
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    /**
     * 初始化侧滑view的高宽
     */
    private void initLeftMain() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int windowsWight = metric.widthPixels;
//        int windowsHeight = metric.heightPixels;
        View leftMenu = findViewById(R.id.leftMain);
        ViewGroup.LayoutParams leftParams = leftMenu.getLayoutParams();
//        leftParams.height = windowsHeight;
        leftParams.width = windowsWight;
        leftMenu.setLayoutParams(leftParams);
        if (Build.VERSION.SDK_INT >= 21) {
//            Log.i(TAG, "initLeftMain: setCarViewElevation !");
//            CardView carView = (CardView) findViewById(R.id.card_view);
//            carView.setCardElevation(15.2f);
        }
    }

    public void init() {
        mContext = this.getApplicationContext();
        userControl = UserControl.getUserControl();
        dialogControl = DialogControl.getDialogControl();
        findid();
        initmap();
        registerListener();
        locatepresener.getcurrentLocation(mlocationClient);//开始定位
    }
    /*初始化map*/
    private void initmap() {
        Log.i(TAG, "initmap: ");
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
        mappresenter = mapPresenter.getmapPresenter();

    }
//************************ 中间的marker *******************************************

    private void addCenterMarker() {
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                Log.i(TAG, "onCameraChange: ");
            }

            @Override
            public void onCameraChangeFinish(CameraPosition postion) {
                Log.i(TAG, "onCameraChangeFinish: ");
                //屏幕中心的Marker跳动
                startJumpAnimation();
            }
        });
    }


    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        addMarkerInScreenCenter();
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
        Log.i(TAG, "startJumpAnimation: ");
        if (screenMarker != null ) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point =  aMap.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(this,80);
//            point.y -= UiUtils.dip2px(80);????转换有问题
            Log.i(TAG, "startJumpAnimation: y is "+point.y);
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
            Log.i(TAG, "startJumpAnimation: startAnim");
        } else {
            Log.e("ama","screenMarker is null");
        }
    }
    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    //*****************************************************************************************


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        Log.i(TAG, "setUpMap: ");
//        aMap.setLocationSource(this);// 设置定位监听
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

    }

    public void findid() {
        mapView = (MapView) findViewById(R.id.mapView);
        location = (Button) findViewById(R.id.main_location);

        userIcn = (CircleImageView) findViewById(R.id.main_user_icn);
        userName = (TextView) findViewById(R.id.main_user_name);
        userMoney = (TextView) findViewById(R.id.money);
        userCash = (TextView) findViewById(R.id.cash);


    }

//*************** listener 相关的方法***********************************

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);
        locatepresener.setlatLonPointLister(this);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        location.setOnClickListener(this);
        findViewById(R.id.rent).setOnClickListener(this);
        //LeftMain
        userIcn.setOnClickListener(this);
        findViewById(R.id.main_setting).setOnClickListener(this);
        findViewById(R.id.user_route).setOnClickListener(this);
        findViewById(R.id.account).setOnClickListener(this);
        findViewById(R.id.service).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_location:
                getLocation();
                break;
            case R.id.rent:
                dialogControl.setDialog(new
                        MarginAlerDialog(MainActivity.this,
                        "保证金提示","请先充值保证金"));
                dialogControl.show();
//                userControl.saoma(MainActivity.this);
                break;
            case R.id.main_user_icn:
                userControl.toChoice(MainActivity.this);
                break;
            case R.id.main_setting:
                userControl.config(MainActivity.this);
                break;
            case R.id.user_route:
                userControl.userRoute(MainActivity.this);
                break;
            case R.id.service:
                userControl.service(MainActivity.this);
                break;
            case R.id.account:
                userControl.account(MainActivity.this);
                break;
            case R.id.share:
                userControl.share(MainActivity.this);
                break;
            default:
                break;
        }
    }
//*************** ****************************************************

    /**
     * 获取当前位置
     */
    private void getLocation() {
        Log.i(TAG, "getLocation: ");
        isFirst = true;
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
        return true;
    }

    //处理menu事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                showToast("change");
                changeCamera(
                        CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                LastPoint, 18, 0, 30)), new myCancelableCallback());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
//**************************************************

    private void showToast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.w(TAG, "--->>>success code is " + resultCode);
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
        Log.w(TAG, " onMarkerClick is show ? " + marker.isInfoWindowShown());
        if (marker.isInfoWindowShown()) {
            {
                Log.i(TAG, "onMarkerClick: hide");
                marker.hideInfoWindow();
            }
        } else {
            Log.i(TAG, "onMarkerClick: show");
            marker.showInfoWindow();
        }

//        if (null != LastPoint) {
//            x = LastPoint.latitude - marker.getPosition().latitude;
//            y = LastPoint.longitude - marker.getPosition().longitude;
//        }
//        Log.w(TAG, " x is " + x + " y is " + y);
        changeCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        marker.getPosition(), 17, 0, 0)), new myCancelableCallback());

//        changeCamera(CameraUpdateFactory.scrollBy(100,100), new myCancelableCallback());
        return false;
    }
//*********************************************************************************

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        Log.i(TAG, "changeCamera: ");
        aMap.animateCamera(update, 1000, callback);

    }
    //*************  LocationSource 的回调方法*****************************************************

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        Log.i(TAG, "activate: ");
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
        }
        locatepresener.getcurrentLocation(mlocationClient);//开始定位

    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        Log.i(TAG, "deactivate: ");
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    //******************************************************************

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
    //**********************搜索路径规划方案********************************************

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            uiUtils.showToast("定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            uiUtils.showToast("终点未设置");
        }

        dialogControl.setDialog(new LocateProgress(MainActivity.this, "正在搜索......"));
        dialogControl.show();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        FAT = fromAndTo;
        if (routeType == ROUTE_TYPE_RIDE) {// 骑行路径规划
            RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateRideRouteAsyn(query);// 异步路径规划骑行模式查询
        }
    }
    //*******************路径规划结果回调***********************************************
    /**
     * 骑行路径结果回调
     *
     * @param result
     * @param errorCode
     */
    @Override
    public void onRideRouteSearched(RideRouteResult result, int errorCode) {
        dialogControl.cancel();
        aMap.clear();// 清理地图上的所有覆盖物
        mRouteDetailDes.setVisibility(View.GONE);
        mappresenter.showRideRoute(result, this, mRotueTimeDes, mBottomLayout
                , errorCode, aMap, FAT);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {

    }
    //******************************************************************

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        showCurrentPosition();
        SensorHelper();
        initUI();
    }
    //*****************  onResume相关方法  ********************************
    /*显示当前位置*/
    private void showCurrentPosition() {
        if(null != mStartPoint){
            LatLng mLatLng = new LatLng(mStartPoint.getLatitude(),mStartPoint.getLongitude());
            changeCamera(
                    CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            mLatLng, 17, 0, 0)), new myCancelableCallback());
        }
    }
    /*判断用户是否处于陆状态*/
    private void initUI() {
        if(null != userControl.getUser()){
            initUserMessage();
        }
        else
            initLogOut();
    }

    /**
     * 初始化用户信息
     */
    private void initUserMessage() {
        userName.setText(userControl.getUser().getUsername());
        userMoney.setText(userControl.getUser().getUsermoney()+"");
        userCash.setText(userControl.getUser().getUsermoney()+"");
    }
    /**
     * 未登录状态
     */
    private void initLogOut() {
        userName.setText("未登录");
        userMoney.setText("0");
        userCash.setText("0");
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
    //******************************************************************
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
        deactivate();
        isFirst = true;
    }
//*******************异常退出保留数据方法*******************************
    /** 异常退出保留数据
     *
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        outState.putInt("IntTest", 0);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int IntTest = savedInstanceState.getInt("IntTest");
    }
//**************************************************

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    /**
     * 定位成功的回调
     *
     * @param
     */
    @Override
    public void getstartlatLonPoint(AMapLocation aMapLocation) {
        mStartPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        Boolean b = dialogControl.getDialog() instanceof LocateProgress;
        Log.i(TAG, "getstartlatLonPoint: is LocateProgress ? " + b +" is first ? "+isFirst);
        if (b) dialogControl.cancel();
//        避免重复定位
        if (isFirst) {
            isFirst = false;
            LastPoint = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            aMap.clear();


            addCircle(LastPoint, aMapLocation.getAccuracy());//添加定位精度圆
            addMarker(LastPoint);//添加定位图标
            mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转

//            aMap.addMarker(new MarkerOptions()
//                    .position(AMapUtil.convertToLatLng(mStartPoint))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
            addCarMarker();
//            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
//            aMap.moveCamera(CameraUpdateFactory.
//                    changeLatLng(new LatLng(mStartPoint.getLatitude(),
//                            mStartPoint.getLongitude())));
            /*移动到当前位置  CameraPosition 第三参数是偏移经度的角度*/
            addMarkersToMap();
            changeCamera(
                    CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            LastPoint, 17, 0, 0)), new myCancelableCallback());

        }
        else if( null != LastPoint){
            mCircle.setCenter(LastPoint);
            mCircle.setRadius(aMapLocation.getAccuracy());
            mLocMarker.setPosition(LastPoint);
        }
    }

    private void addCarMarker() {
        Log.i(TAG, "addCarMarker: ");
        for (int i = 0; i < points.length; i++) {
            MarkerOptions markerOptions = new MarkerOptions()
//                       .icon(BitmapDescriptorFactory.fromView(view))
                    .title("车牌号:2525")
                    .snippet("可行驶里程: " + i + " 公里")
                    .position(AMapUtil.convertToLatLng(points[i]))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
            aMap.addMarker(markerOptions);
        }
    }


    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }
    private void addMarker(LatLng latlng) {
        Log.i(TAG, "addMarker: ");
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
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
        if (null != point)
            mEndPoint = point;
        dialogControl.cancel();
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
        searchRouteResult(ROUTE_TYPE_RIDE, RouteSearch.RidingDefault);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
