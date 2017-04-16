package com.joshua.a51bike.activity.core;

import android.os.Bundle;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RouteSearch;
import com.joshua.a51bike.Interface.latLonPointInterface;
import com.joshua.a51bike.activity.presenter.locatePresenter;
import com.joshua.a51bike.activity.presenter.mapPresenter;

/**
 * Created by wangqiang on 2017/1/8.
 */
public abstract class BaseMap extends BaseActivity implements
        AMap.OnMarkerClickListener,latLonPointInterface,
        AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener  {
    public locatePresenter locatepresener;
    public mapPresenter mappresenter = mapPresenter.getmapPresenter();
    public RouteSearch mRouteSearch;
    public LatLonPoint mStartPoint ;//起点
    public LatLonPoint mEndPoint;//终点
    public   RouteSearch.FromAndTo FAT;
    public final int ROUTE_TYPE_RIDE = 4;
    public UiSettings mUiSettings;
    protected AMapLocationClient mlocationClient;
    public  final int GPS_REQUESTCODE = 1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}