package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.amap.api.location.AMapLocation;
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
import com.amap.api.services.route.WalkRouteResult;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseMap;
import com.joshua.a51bike.util.AMapUtil;

import org.xutils.view.annotation.ContentView;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-01
 */
@ContentView(R.layout.user_route_mes)
public class UserRouteMes extends BaseMap {
    private static final String TAG = "UserRouteMes";
    private CheckBox checkBox;
    private View blackView;
    private   BottomSheetBehavior behavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        findId();
        setLister();
        initmap();
    }

    public void findId() {
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        blackView = findViewById(R.id.blackview);
        blackView.setBackgroundColor(Color.parseColor("#60000000"));
        blackView.setVisibility(View.GONE);
        blackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }


    public void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);
        behavior = BottomSheetBehavior.from(findViewById(R.id.scroll));
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.i(TAG, "onStateChanged: newState"+ newState +" "+STATE_HIDDEN +" "+STATE_EXPANDED);
                if(newState == STATE_COLLAPSED){//被隐藏
                    {
//                        getWindow().setBackgroundDrawableResource(R.color.white);
                        blackView.setVisibility(View.GONE);

                        checkBox.setChecked(false);

                    }
                }else if(newState == STATE_EXPANDED)//显示出来
                {
//                    getWindow().setBackgroundDrawableResource(R.color.gray);
                    blackView.setVisibility(View.VISIBLE);
                    checkBox.setChecked(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                ViewCompat.setAlpha(blackView,slideOffset);

            }
        });
    }
    private void initmap() {
        mapView = (MapView) findViewById(R.id.route_mapView);

        if (aMap == null) {
            Log.w(TAG,"------>> aMap is null");
            aMap = mapView.getMap();
        }
        aMap.clear();
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
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
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * Called when an activity you launched exits
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


    @Override
    public void getstartlatLonPoint(AMapLocation aMapLocation) {

    }

    @Override
    public void getEndlatLonPoint(LatLonPoint point) {

    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
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

}