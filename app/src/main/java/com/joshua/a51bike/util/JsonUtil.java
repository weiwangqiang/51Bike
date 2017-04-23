package com.joshua.a51bike.util;

import com.google.gson.Gson;
import com.joshua.a51bike.entity.Car;
import com.joshua.a51bike.entity.Order;
import com.joshua.a51bike.entity.ReadData;
import com.joshua.a51bike.entity.User;

/**
 * class description here
 *  json 帮助类
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-02-06
 */
public class JsonUtil {
    private static String TAG = "JsonUtil";
    public static Gson gson = new Gson();

    public static String getJson(Object object){
        return  gson.toJson(object);
    }
    /**
     * 由json获取user对象
     * @param result
     * @return
     */
    public static User getUserObject(String result){
        User user = null;
        try{
            user = gson.fromJson(result,User.class);
        }catch(Exception e){

        }
        return user;
    }
    /**
     * 由json获取car对象
     * @param result
     * @return
     */
    public static Car getCarObject(String result){
        Car car = null;
        try{
            car = gson.fromJson(result,Car.class);
        }catch(Exception e){

        }
        return car;
    }

    /**
     * 由string解析为ReadData
     * @param result
     * @return
     */
    public static ReadData getReadDataByString(String result){
        ReadData readData = null;
            try{
                readData = gson.fromJson(result,ReadData.class);
                }catch(Exception e){
                    e.printStackTrace();
                }
        return readData;
    }

    /**
     * 根据result 返还order对象
     * @param result 要解析对象
     * @return 返回的对象
     */
    public static Order getOrderByString(String result){
        Order order = null;
            try{
               order = gson.fromJson(result,Order.class);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return order;
    }
}
