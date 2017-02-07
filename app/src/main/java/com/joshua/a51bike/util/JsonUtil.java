package com.joshua.a51bike.util;

import android.util.Log;

import com.google.gson.Gson;
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
            Log.d(TAG, "Login: json is "+ jsonObject.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 由json获取user对象
     * @param json
     * @return
     */
    public static User getUserObject(String json){
        return gson.fromJson(json,User.class);
    }

}
