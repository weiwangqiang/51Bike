package com.joshua.a51bike.Interface;

import android.app.Activity;

import com.amap.api.maps.AMap;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;

/**
 * 显示路径规划的接口
 * Created by wangqiang on 2017/1/3.
 */

public interface mapInterface {
    /**
     * 显示路径规划的方法
     * @param result 路径规划结果
     * @param activity 上下文
     * @param errorCode 报错码
     * @param map map相关
     * @param fromAndTo 起始信息
     */
    void showRideRoute(RideRouteResult result, Activity activity,
                       int errorCode, AMap map, final RouteSearch.FromAndTo fromAndTo);

}
