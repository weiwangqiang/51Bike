package com.joshua.a51bike.adapter;


import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * class description here
 *
 * gson解析中，将long类型转为Timestamp 的类
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-04-20
 */
public class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
    String TAG = "TimestampTypeAdapter";
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public JsonElement serialize(Timestamp ts, Type t, JsonSerializationContext jsc) {
        String dfString = format.format(new Date(ts.getTime()));
        return new JsonPrimitive(dfString);
    }
    public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }

        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long L = Long.valueOf(json.getAsString());
            Log.i(TAG, "deserialize: -------------------------");
            Log.i(TAG, "deserialize:   : "+json.getAsString());
            Log.i(TAG, "deserialize: L : "+L);
            Log.i(TAG, "deserialize: -------------------------");
            return new Timestamp(L);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
