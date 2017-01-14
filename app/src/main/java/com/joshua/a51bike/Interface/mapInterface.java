package com.joshua.a51bike.Interface;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;

/**
 * Created by wangqiang on 2017/1/3.
 */

public interface mapInterface {
    void showRideRoute(RideRouteResult result, Activity activity, TextView view, RelativeLayout layout,
                       int errorCode, AMap map, final RouteSearch.FromAndTo fromAndTo);
    void showPoi(Poi poi, Context context, AMap aMap);
}
