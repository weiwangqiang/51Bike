package com.joshua.a51bike.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
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
import com.joshua.a51bike.activity.presenter.locatePresenter;
import com.joshua.a51bike.activity.view.Use_Explain;
import com.joshua.a51bike.activity.view.searchBike;
import com.joshua.a51bike.customview.CircleImageView;
import com.joshua.a51bike.customview.progress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AMapUtil;
import com.joshua.a51bike.util.SensorEventHelper;
import com.joshua.a51bike.util.imageUtil.ImageManager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseMap {
    public String TAG = "MainActivity";
    private CircleImageView userIcn;
    private TextView userName,userMoney,userCash;
    private DrawerLayout drawer;
    private Context mContext;
    private Button location;
    public LatLng LastPoint;
    //canGetPos 是否可以更新当前位置
    private Boolean canGetPos = true,canShow = false;
    private progress myProgress;
    private Marker mLocMarker;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    private  Toolbar toolbar ;
    public LatLonPoint point1 = new LatLonPoint(32.1979265479926, 119.51321482658388);
    public LatLonPoint point2 = new LatLonPoint(32.19794016630354, 119.51738834381104);
    public LatLonPoint point3 = new LatLonPoint(32.20268375393801, 119.51433062553406);
    public LatLonPoint[] points = new LatLonPoint[]{point1, point2, point3};
    private  MapView mapView;
    private  AMap aMap;

    @ViewInject(R.id.main_use_explain)
    private TextView explain;

    private View useProgressParent;

    @ViewInject(R.id.main_progress_view)
    private progress useProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: task id is "+getTaskId());
        init();
        if(savedInstanceState == null)
            canShow = true;
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initDrawer();

    }
    private void initDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        View leftMenu = findViewById(R.id.leftMain);
        ViewGroup.LayoutParams leftParams = leftMenu.getLayoutParams();
        findViewById(R.id.left_back).setOnClickListener(this);
        leftParams.width = windowsWight;
        leftMenu.setLayoutParams(leftParams);
    }
    private TextView textView1,textView2,textView3,textView4;
    private List<TextView> list = new ArrayList<>();


    private void showProgreesOrNot(User user) {
        setProgressView(1);//更新
        if(user == null ){
            Log.i(TAG, "showProgreesOrNot: user is null ");
            useProgressParent.setVisibility(View.GONE);
            return;
        }
        if(useProgressParent.getVisibility() == View.GONE)
            useProgressParent.setVisibility(View.VISIBLE);
        if(user.getRealName() != null )
            setProgressView(2);
        if(user.getUsermoney() != 0)
            useProgressParent.setVisibility(View.GONE);
    }

    private void setProgressView(int a) {

        useProgress.setPoistion(a);
        int i ;
        for(TextView T:list){
            T.setTextColor(getResources().getColor(R.color.gray));
        }
        for(i = 0;i<a;i++){
            list.get(i).setTextColor(getResources().getColor(R.color.baseColor));
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
                    Log.i(TAG, "onCameraChangeFinish: x "+x+" y "+y);
                    if(x > 0.005 || y > 0.005){
                        startJumpAnimation();
                    }
                }
                lastPosition = nowPosition;
                Log.i(TAG, "onCameraChangeFinish: ");
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
//        aMap.setLocationSource(this);// 设置定位监听
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

    }

    public void findid() {
        mapView = (MapView) findViewById(R.id.main_mapView);
        location = (Button) findViewById(R.id.main_location);

        useProgressParent = findViewById(R.id.main_progress);

        userIcn = (CircleImageView) findViewById(R.id.main_user_icn);
        userName = (TextView) findViewById(R.id.main_user_name);
        userMoney = (TextView) findViewById(R.id.money);
        userCash = (TextView) findViewById(R.id.cash);
        myProgress = (progress) findViewById(R.id.main_progress_view);

        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView3 = (TextView) findViewById(R.id.text3);
        textView4 = (TextView) findViewById(R.id.text4);

        list.add(textView1);
        list.add(textView2);
        list.add(textView3);
        list.add(textView4);

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
        explain.setOnClickListener(this);
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
//                dialogControl.setDialog(new
//                        MarginAlerDialog(MainActivity.this,
//                        "保证金提示","请先充值保证金"));
//                dialogControl.show();
                userControl.saoma(MainActivity.this);
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
            case R.id.main_use_explain:
             startActivity(new Intent(this, Use_Explain.class));
                break;
            case R.id.left_back:
                if(drawer.isDrawerOpen(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
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
        showCurrentPosition();
        //获取最新地理位置
        canGetPos = true;
        if(mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
        }
        locatepresener.getcurrentLocation(mlocationClient);//开始定位
//        dialogControl.setDialog(new LocateProgress(MainActivity.this, "正在搜索......"));
//        dialogControl.show();
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
                toSearchActivty();
            case android.R.id.home:
                Log.i(TAG, "onOptionsItemSelected: home ");

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toSearchActivty() {
      startActivity(new Intent(this,searchBike.class));

    }
//**************************************************

    private void showToast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

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
        } else {
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
        aMap.animateCamera(update, 1000, callback);

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
        mappresenter.showRideRoute(result, this
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
    //***********************生命周期*******************************************
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

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
        if(canShow){
            showDialog();
            canShow = false;
        }

    }
    private void showDialog() {
        Log.i(TAG, "onCreate: saveInstanceState is null");
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
        Log.i(TAG, "onPause: ");
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mapView.onPause();
        canGetPos = true;
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
//*******************异常退出保留数据方法*******************************
    /** 异常退出保留数据
     *
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
        mapView.onSaveInstanceState(outState);
        outState.putInt("IntTest", 0);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState: ");
        int IntTest = savedInstanceState.getInt("IntTest");
    }

    //***************** g更新ui  ********************************
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
    String pre_image_path = Environment.getExternalStorageDirectory()+"/51get";

    private void initUserMessage() {
//        String  after_image_path = pre_image_path +"/"+userControl.getUser().getUsername()+".jpg";
        User user = userControl.getUser();
        String after_image_path = "";


        userName.setText(userControl.getUser().getUsername());
        userMoney.setText(userControl.getUser().getUsermoney()+"");
        userCash.setText(userControl.getUser().getUsermoney()+"");
        showProgreesOrNot(user);
        if(after_image_path == null )
            return;
        after_image_path = pre_image_path +"/"+userControl.getUser().getUsername()+".jpg";
        Log.i(TAG, "initUserMessage: afte_image_uri"+after_image_path);
        ImageManager manager = new  ImageManager();
        if(userControl.getUser().getUserpic() != null){
            manager.bindImageWithBitmap(userIcn,
                    after_image_path);
        }
    }


    /**
     * 未登录状态
     */
    private void initLogOut() {
        userName.setText("未登录");
        userMoney.setText("0");
        userCash.setText("0");
        setProgressView(0);
        if(useProgressParent.getVisibility() == View.GONE)
            useProgressParent.setVisibility(View.VISIBLE);

        userIcn.setImageResource(R.drawable.default_icn);
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


//**************************************************

    /**
     * 定位成功的回调
     *
     * @param
     */
    @Override
    public void getstartlatLonPoint(AMapLocation aMapLocation) {
        mStartPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        Boolean b = dialogControl.getDialog() instanceof LocateProgress;
        if (b) dialogControl.cancel();
//        避免重复定位
        if (canGetPos) {
            canGetPos = false;
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

}
