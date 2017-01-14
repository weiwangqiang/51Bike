package com.joshua.a51bike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
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
import com.joshua.a51bike.activity.presenter.mapPresenter;
import com.joshua.a51bike.activity.view.CircleImageView;
import com.joshua.a51bike.activity.view.Config;
import com.joshua.a51bike.util.AMapUtil;
import com.joshua.a51bike.util.ToastUtil;

import org.xutils.view.annotation.ContentView;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseMap {
    public String TAG = "MainActivity";
    private CircleImageView userIcn;
    private DrawerLayout drawer;
    private MapView mapView;
    private Context mContext;
    private Button location;
    public LatLng LastPoint;
    private double x;
    private double y;
    private UserControl userControl;
    private DialogControl dialogControl;
    public LatLonPoint point1 = new LatLonPoint(32.1979265479926, 119.51321482658388);
    public LatLonPoint point2 = new LatLonPoint(32.19794016630354, 119.51738834381104);
    public LatLonPoint point3 = new LatLonPoint(32.20268375393801, 119.51433062553406);
    public LatLonPoint[] points = new LatLonPoint[]{point1, point2, point3};


    //    latitude is 32.1979265479926 longitude is 119.51321482658388
//latitude is 32.19794016630354 longitude is 119.51738834381104
//    ----->latitude is 32.20268375393801 longitude is 119.51433062553406
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initDrawer();
    }

    private void initDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //实现左右滑动
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    public void init() {
        mContext = this.getApplicationContext();
        userControl = UserControl.getUserControl();
        dialogControl = DialogControl.getDialogControl();
        findid();
        initmap();
    }

    private void initmap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        }
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        locatepresener = locatePresenter.getlocation();
        mappresenter = mapPresenter.getmapPresenter();
        registerListener();
    }

    public void findid() {
        mapView = (MapView) findViewById(R.id.mapView);
        location = (Button) findViewById(R.id.main_location);

        userIcn = (CircleImageView) findViewById(R.id.main_user_icn);
    }

    private void test() {
        for(int i = 0;i <10 ; i++){
            new String("test");

        }
    }


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

        dialogControl.setDialog(new GPSAlerDialog(MainActivity.this));
        dialogControl.show();

        locatepresener.setcurrentLocation(this);//开始定位

        userIcn.setOnClickListener(this);
        findViewById(R.id.main_setting).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_location:
                getLocation();
                break;
            case R.id.rent:
                userControl.saoma(MainActivity.this);
                break;
            case R.id.main_user_icn:
                userControl.toChoice(MainActivity.this);
                break;
            case R.id.main_setting:
                Intent intent = new Intent(MainActivity.this, Config.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 获取当前位置
     */
    private void getLocation() {
        isFirst = true;
        locatepresener.setcurrentLocation(this);//开始定位
        dialogControl.setDialog(new LocateProgress(MainActivity.this, "正在搜索......"));
        dialogControl.show();
    }

    //处理menu事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                showToast("search");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    //引入menu布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
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
            Toast.makeText(mBaseActivity, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        } else {//退出程序
            Intent intent = new Intent("com.joshua.exit");
            sendBroadcast(intent);
        }
    }

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
        Log.w(TAG, " is show ? " + marker.isInfoWindowShown());
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else marker.showInfoWindow();

        if (null != LastPoint) {
            x = LastPoint.latitude - marker.getPosition().latitude;
            y = LastPoint.longitude - marker.getPosition().longitude;
        }
        LastPoint = marker.getPosition();
        Log.w(TAG, " x is " + x + " y is " + y);
        changeCamera(CameraUpdateFactory.scrollBy((float) 100, (float) 100), null);
        return false;
    }


    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        aMap.animateCamera(update, 2000, callback);

    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
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

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if(null != mStartPoint){
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            aMap.moveCamera(CameraUpdateFactory.
                    changeLatLng(new LatLng(mStartPoint.getLatitude(),
                            mStartPoint.getLongitude())));
        }
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

    /**
     * 获取当前地理位置的回调
     *
     * @param point
     */
    @Override
    public void getstartlatLonPoint(LatLonPoint point) {
        mStartPoint = point;
        Boolean b = dialogControl.getDialog().getClass() == (LocateProgress.class);
        if (b) {
            dialogControl.cancel();
        }
        //避免重复定位
//        View view = UiUtils.inflate(R.layout.activity_main);
        if (isFirst) {
            aMap.clear();
            aMap.addMarker(new MarkerOptions()
                    .position(AMapUtil.convertToLatLng(mStartPoint))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
            LastPoint = new LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude());
            for (int i = 0; i < points.length; i++) {

                MarkerOptions markerOptions = new MarkerOptions()
//                       .icon(BitmapDescriptorFactory.fromView(view))
                        .title("车牌号:2525")
                        .snippet("可行驶里程: " + i + " 公里")
                        .position(AMapUtil.convertToLatLng(points[i]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
                aMap.addMarker(markerOptions);
            }
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            aMap.moveCamera(CameraUpdateFactory.
                    changeLatLng(new LatLng(mStartPoint.getLatitude(),
                            mStartPoint.getLongitude())));
            isFirst = false;
        }
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
