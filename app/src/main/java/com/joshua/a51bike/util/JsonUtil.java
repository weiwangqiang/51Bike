package com.joshua.a51bike.util;

import com.google.gson.Gson;
import com.joshua.a51bike.entity.Car;
import com.joshua.a51bike.entity.ReadData;
import com.joshua.a51bike.entity.User;

import org.json.JSONException;
import org.json.JSONObject;

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

    /**
     * 由对象获取jsonObject
     * @param object
     * @return
     */
    public static JSONObject getJSONObject(Object object){
        String json = gson.toJson(object);
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(json);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static String getJson(Object object){
        return  gson.toJson(object);
    }
    /**
     * 由json获取user对象
     * @param json
     * @return
     */
    public static User getUserObject(String json){
        User user = null;
        try{
            user = gson.fromJson(json,User.class);
        }catch(Exception e){

        }
        return user;
    }
    /**
     * 由json获取car对象
     * @param json
     * @return
     */
    public static Car getCarObject(String json){
        Car car = null;
        try{
            car = gson.fromJson(json,Car.class);
        }catch(Exception e){

        }
        return car;
    }

    public static ReadData getReadDataByString(String string){
        ReadData readData = null;
            try{
                readData = gson.fromJson(string,ReadData.class);
                }catch(Exception e){
                    e.printStackTrace();
                }
        return readData;
    }
}
