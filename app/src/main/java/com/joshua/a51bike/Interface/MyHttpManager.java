package com.joshua.a51bike.Interface;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by wangqiang on 2017/2/7.
 */

public interface MyHttpManager {
    void success(JSONObject object);
    void error(VolleyError volleyError);
}
