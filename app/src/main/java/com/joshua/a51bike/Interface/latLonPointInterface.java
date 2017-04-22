package com.joshua.a51bike.Interface;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;

/**
 * 获取start end点的latLonPoint接口
 * Created by wangqiang on 2017/1/3.
 */

public interface latLonPointInterface {
    /**
     * 获取当前位置的方法
     * @param aMapLocation 返回位置信息
     */
    void getstartlatLonPoint(AMapLocation aMapLocation);

    /**
     * 获取目的地的位置方法
     * @param point 返回 目的地的位置信息
     */
    void getEndlatLonPoint(LatLonPoint point);

    /**
     * 获取信息异常的回调
     */
    void Error();
}
