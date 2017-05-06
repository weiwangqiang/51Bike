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
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseMap;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.activity.overlay.RideRouteOverlay;
import com.joshua.a51bike.adapter.TimestampTypeAdapter;
import com.joshua.a51bike.entity.UserAndUse;
import com.joshua.a51bike.util.AppUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.sql.Timestamp;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static com.alipay.sdk.app.statistic.c.r;

/**
 * class description here
 *
 *用户的历史租车轨迹详情界面
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-01
 */
@ContentView(R.layout.user_route_mes)
public class UserRouteMes extends BaseMap implements RouteSearch.OnRouteSearchListener {
    private static final String TAG = "UserRouteMes";
    private CheckBox checkBox;
    private View blackView;
    private   BottomSheetBehavior behavior;
    private  MapView mapView;
    private  AMap aMap;
    private boolean isfirst = true;
    private LatLonPoint mStartPoint ;
    private LatLonPoint mEndPoint;
    private final int ROUTE_TYPE_RIDE = 4;
    private  RouteSearch.FromAndTo FAT;

    @ViewInject(R.id.route_item_spend)
    private TextView route_item_spend;

    @ViewInject(R.id.route_item_carId)
    private TextView route_item_carId;

    @ViewInject(R.id.route_item_useTime)
    private TextView route_item_useTime;

    @ViewInject(R.id.route_item_totalMoney)
    private TextView route_item_totalMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartPoint = new LatLonPoint(32.1979265479926, 119.51321482658388);
        mEndPoint =new LatLonPoint(32.19794016630354, 119.51738834381104);
        init();
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        getBikeMes();
    }
    private String id ;
    public void init() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        findId();
        setLister();
        initmap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.moveCamera(CameraUpdateFactory.
                changeLatLng(new LatLng(mStartPoint.getLatitude(),
                        mStartPoint.getLongitude())));
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
        findViewById(R.id.see_ChargingRule).setOnClickListener(this);
        behavior = BottomSheetBehavior.from(findViewById(R.id.scroll));
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.i(TAG, "onStateChanged: newState"+ newState +" "+STATE_HIDDEN +" "+STATE_EXPANDED);
                if(newState == STATE_COLLAPSED){//被隐藏
                    {
                        getWindow().setBackgroundDrawableResource(R.color.white);
                        blackView.setVisibility(View.GONE);
                        checkBox.setChecked(false);

                    }
                }else if(newState == STATE_EXPANDED)//显示出来
                {
                    getWindow().setBackgroundDrawableResource(R.color.gray);
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

    /**
     * 通过服务器获取用户历史骑车信息
     */
    private String url = AppUtil.BaseUrl+"/car/getOrderById";
    public void getBikeMes(){
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("id",id);
        Log.i(TAG, "getBikeMes: params ---"+params.toString());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: -------------------");
                Log.i(TAG, "onSuccess: "+result);
                parseResult(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: --------------------");
                ex.printStackTrace();
//              1handler.sendEmptyMessage(NET_ERROR);
                dialogControl.cancel();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: cancel", null);
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {
                dialogControl.cancel();

            }
        });
    }

    /**
     * 更新订单详情界面
     */
    private void upOrderMes() {
        route_item_spend.setText(use.getUseMoney()+"");
        route_item_carId.setText(use.getCarId()+"");
        route_item_useTime.setText(use.getUseHour().toString());
        route_item_totalMoney.setText(use.getUseMoney()+"");
        searchRouteResult(ROUTE_TYPE_RIDE, RouteSearch.RidingDefault);
    }

    private  UserAndUse use = null;
    private void parseResult(String result) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
        gsonBuilder.registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter());
        Gson gson = gsonBuilder.create();
        try {
            use = gson.fromJson(result,UserAndUse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(null == use ){
            dialogControl.cancel();
            uiUtils.showToast("获取失败！");
        }
        else
            upOrderMes();
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
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
    }


    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        Log.i(TAG, "searchRouteResult: ");
        if (mStartPoint == null) {
            uiUtils.showToast( "定位中，稍后再试...");
            dialogControl.cancel();
            return;
        }
        if (mEndPoint == null) {
            dialogControl.cancel();
            uiUtils.showToast( "终点未设置");
        }
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        FAT = fromAndTo;
        if (routeType == ROUTE_TYPE_RIDE) {// 骑行路径规划
            Log.i(TAG, "searchRouteResult: start search route ");
            RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateRideRouteAsyn(query);// 异步路径规划骑行模式查询
        }
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
            case  R.id.see_ChargingRule:
                Intent intent = new Intent(this,ChargingRule.class);
                startActivity(intent);
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
    public void getstartlatLonPoint(AMapLocation aMapLocation) {

    }

    @Override
    public void getEndlatLonPoint(LatLonPoint point) {

    }

    @Override
    public void Error() {

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
        if(isfirst){
            dialogControl.setDialog(new WaitProgress(this));
            dialogControl.show();
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
    public void onRideRouteSearched(RideRouteResult result, int errorCode) {
        Log.i(TAG, "onRideRouteSearched: ==========回调");
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (r != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    final RidePath ridePath = result.getPaths()
                            .get(0);
                    RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                            UserRouteMes.this ,aMap, ridePath,
                            result.getStartPos(),
                            result.getTargetPos());
                    rideRouteOverlay.removeFromMap();
                    rideRouteOverlay.addToMap();
                    rideRouteOverlay.zoomToSpan();
                } else if (r != null && result.getPaths() == null) {
                    uiUtils.showToast("对不起，没有搜索到相关数据！");

                }
            } else {
                uiUtils.showToast("对不起，没有搜索到相关数据！");
            }
        }
    }
}