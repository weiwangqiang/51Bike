package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.util.AMapUtil;
import com.joshua.a51bike.activity.dialog.LocateProgress;

import org.xutils.view.annotation.ContentView;



/**
 * Created by wangqiang on 2017/1/9.
 */
@ContentView(R.layout.bike_message)
public class BikeMessage extends BaseActivity {
    private Button rent;
    private String TAG = "BikeMessage";
    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    private DialogControl dialogControl;
    private UserControl userControl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mapView.onCreate(savedInstanceState); // 此方法必须重写
    }

    private void init() {
        findid();
        initmap();
        setLister();
    }

    private void setLister() {
        rent.setOnClickListener(this);
        findViewById(R.id.left_back).setOnClickListener(this);

    }

    private void findid() {
        rent = (Button) findViewById(R.id.bike_mes_rent);
        mapView = (MapView) findViewById(R.id.bike_mes_mapView);

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
                userControl = UserControl.getUserControl();
                userControl.rent(BikeMessage.this);
            break;
            default:
                break;
        }
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