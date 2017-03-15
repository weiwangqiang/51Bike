package com.joshua.a51bike.Interface;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;

/**获取start end点的latLonPoint接口
 * Created by wangqiang on 2017/1/3.
 */

public interface latLonPointInterface {
//    void getstartlatLonPoint(LatLonPoint point);
    void getstartlatLonPoint(AMapLocation aMapLocation);
    void getEndlatLonPoint(LatLonPoint point);
}
