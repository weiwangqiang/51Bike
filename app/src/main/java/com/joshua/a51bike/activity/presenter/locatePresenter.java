package com.joshua.a51bike.activity.presenter;

import android.app.Activity;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.joshua.a51bike.Interface.latLonPointInterface;
import com.joshua.a51bike.util.UiUtils;

import java.util.List;


/**
 * Created by wangqiang on 2017/1/3.
 */

public class locatePresenter implements AMapLocationListener,
        GeocodeSearch.OnGeocodeSearchListener {
        String TAG = "locatePresenter";
    public AMapLocationClientOption mLocationOption = null;
    private latLonPointInterface res;
    private boolean canShow = true;
    private static locatePresenter locatePres= new locatePresenter();
    public static locatePresenter Instance(){
        return locatePres;
    }

    /**
     * 设置latLonPointInterface回调
     * @param res
     */
    public void setlatLonPointLister(latLonPointInterface res){
        this.res = res;
    }
    public void setgeoCodeSearch(Activity activity, String area, String city) {
        // 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        GeocodeQuery query = new GeocodeQuery(area,city);
        GeocodeSearch geocodeSearch = new GeocodeSearch(activity);
        geocodeSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
        geocodeSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 获取当前地理位置
     * @param mLocationClient
     */
    public void getcurrentLocation(AMapLocationClient mLocationClient){
        Log.i(TAG, "getcurrentLocation: ");
        if(null == mLocationClient) return;
        // 设置定位回调监听，这里要实现AMapLocationListener接口，
        // AMapLocationListener接口只有onLocationChanged方法可以实现，
        // 用于接收异步返回的定位结果，参数是AMapLocation类型。
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        Log.i(TAG, "getcurrentLocation: start location");
        canShow = true;
    }
    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
//                aMapLocation.getLatitude();//获取纬度
//                aMapLocation.getLongitude();//获取经度
                //获取当前位置
//                LatLonPoint mStartPoint = new LatLonPoint(aMapLocation.getLatitude(),
//                        aMapLocation.getLongitude());
                res.getstartlatLonPoint(aMapLocation);
            }else if(canShow){
                UiUtils.showToast("定位失败，请重试");
                canShow = false;
            }
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (1000==i){
            List<GeocodeAddress> list;
            list = geocodeResult.getGeocodeAddressList();
            LatLonPoint ends = new LatLonPoint(list.get(0)
                    .getLatLonPoint().getLatitude(),
                    list.get(0).getLatLonPoint().getLongitude());
            //创建目的地的纬度经度
            res.getEndlatLonPoint(ends);
        }
    }
}
