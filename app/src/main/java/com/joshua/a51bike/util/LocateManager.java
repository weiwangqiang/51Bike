package com.joshua.a51bike.util;

import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-04-27
 */
public class LocateManager {
    private String TAG = "LocateManager";
    private LatLng center = new LatLng(32.19281953769914 ,119.52218413352966);
    private   PolygonOptions pOption = new PolygonOptions();
    public LocateManager(AMap aMap) {

        // 绘制一个长方形
        pOption.add(new LatLng(32.20823502612345, 119.50961530208588));
        pOption.add(new LatLng(32.208153325473255, 119.51200783252717));
        pOption.add(new LatLng(32.2007364163075, 119.52268838882448));
        pOption.add(new LatLng(32.19371385264583 , 119.52711403369902));
        pOption.add(new LatLng(32.19281953769914 , 119.52218413352966));

        pOption.add(new LatLng(32.19356858381069, 119.52173888683319));
        pOption.add(new LatLng(32.19505303879744 , 119.50873553752899));
        pOption.add(new LatLng(32.19674174713814, 119.50664341449739));
        pOption.add(new LatLng(32.20097245945238, 119.50707793235782));

        pOption.add(new LatLng(32.20815786440019 , 119.50967967510225));

        polygon = aMap.addPolygon(pOption.strokeWidth(4)
                .strokeColor(Color.argb(50, 1, 1, 1))
                .fillColor(Color.argb(50, 1, 1, 1)));
    }
    private Polygon polygon;

    public boolean isInSchool(LatLng point){
      return  polygon.contains(point);
    }
}
