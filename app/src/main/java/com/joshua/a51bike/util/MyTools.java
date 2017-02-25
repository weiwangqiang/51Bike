package com.joshua.a51bike.util;

import android.widget.EditText;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-02-05
 */
public class MyTools {
    private String TAG = "MyTools";
    public static Boolean EditTextIsNull(EditText editText){
        return editText.getText().toString().length() == 0;
    }

}
