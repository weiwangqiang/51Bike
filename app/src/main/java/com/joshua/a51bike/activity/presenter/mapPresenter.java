package com.joshua.a51bike.activity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.joshua.a51bike.Interface.mapInterface;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.view.RideRouteDetailActivity;
import com.joshua.a51bike.activity.overlay.RideRouteOverlay;
import com.joshua.a51bike.util.AMapUtil;
import com.joshua.a51bike.util.ToastUtil;
/**
 * Created by wangqiang on 2017/1/3.
 */

public class mapPresenter implements mapInterface {
    private String TAG = "mapPresenter";
    private static mapPresenter map = new mapPresenter();
    public static mapPresenter getmapPresenter(){
        return map;
    }
    @Override
    public void showRideRoute(final RideRouteResult r, final Activity activitty,
                              TextView t, RelativeLayout L, int errorCode,
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
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    t.setText(des);
                    L.setVisibility(View.VISIBLE);
                    L.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activitty,
                                    RideRouteDetailActivity.class);
                            intent.putExtra("ride_path", ridePath);
                            intent.putExtra("ride_result",
                                    r);
                            intent.putExtra("fromAndTo",fromAndTo);
                            activitty.startActivity(intent);
                        }
                    });
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

    @Override
    public void showPoi(Poi poi, Context context, AMap aMap) {

        Log.i("MY", poi.getPoiId()+poi.getName());
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(poi.getCoordinate());
        TextView textView = new TextView(context);
        textView.setText("到"+poi.getName()+"去");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundResource(R.drawable.custom_info_bubble);
        markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
        aMap.addMarker(markOptiopns);
        Log.e(TAG," ----->latitude is "+markOptiopns.getPosition().latitude
                +" longitude is "+markOptiopns.getPosition().longitude);
    }
}
