package com.joshua.a51bike.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.joshua.a51bike.Interface.MyHttpManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-02-07
 */
public class VolleyUtil extends AppUtil {
    public RequestQueue requestQueue;
    private MyHttpManager manager;
    private String TAG = "VolleyUtil";

    private Context context;
    public VolleyUtil(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }
    public  void setManager(MyHttpManager manager){
        this.manager = manager;

    }
    public void postjson(String url ,JSONObject object) {
        Log.d(TAG, "postjson: volley post");
        Log.d(TAG, "postjson: object is "+object.toString());
        JsonRequest<JSONObject> jsonResquest =
                new JsonObjectRequest(Request.Method.POST, url, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject object) {
                                manager.success(object);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                             manager.error(volleyError);
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("dat    aType", "json");
                        headers.put("ContentType", "application/json; charset=UTF-8");
                        return headers;
                    }
                };
        requestQueue.add(jsonResquest);
    }
}
