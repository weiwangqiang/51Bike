package com.joshua.a51bike.activity.presenter;

import android.app.Activity;

import com.amap.api.maps.AMap;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.joshua.a51bike.Interface.mapInterface;
import com.joshua.a51bike.activity.overlay.RideRouteOverlay;
import com.joshua.a51bike.util.ToastUtil;
/**
 * Created by wangqiang on 2017/1/3.
 * 规划路径相关的代理
 */

public class mapPresenter implements mapInterface {
    private String TAG = "mapPresenter";
    private static mapPresenter map = new mapPresenter();
    public static mapPresenter getmapPresenter(){
        return map;
    }
    @Override
    public void showRideRoute(final RideRouteResult r, final Activity activitty,
                             int errorCode,
                              AMap a, final RouteSearch.FromAndTo fromAndTo) {

        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (r != null && r.getPaths() != null) {
                if (r.getPaths().size() > 0) {
                    final RidePath ridePath = r.getPaths()
                            .get(0);
                    RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                            activitty, a, ridePath,
                            r.getStartPos(),
                            r.getTargetPos());
                    rideRouteOverlay.removeFromMap();
                    rideRouteOverlay.addToMap();
                    rideRouteOverlay.zoomToSpan();
                    int dis = (int) ridePath.getDistance();
                    int dur = (int) ridePath.getDuration();
                } else if (r != null && r.getPaths() == null) {
                    ToastUtil.show(activitty, "对不起，没有搜索到相关数据！");

                }
            } else {
                ToastUtil.show(activitty,"对不起，没有搜索到相关数据！");
            }
        } else {
            ToastUtil.showerror(activitty, errorCode);
        }

    }
}
