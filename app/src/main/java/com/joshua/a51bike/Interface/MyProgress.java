package com.joshua.a51bike.Interface;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by wangqiang on 2017/1/9.
 */

public abstract class MyProgress extends ProgressDialog implements DialogInterface{
    public MyProgress(Context context) {
        super(context);
    }
}
