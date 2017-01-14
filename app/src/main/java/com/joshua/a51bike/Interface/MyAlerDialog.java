package com.joshua.a51bike.Interface;

import android.app.AlertDialog;
import android.content.Context;

/**
 *
 * Created by wangqiang on 2017/1/9.
 */

public abstract class MyAlerDialog extends AlertDialog implements DialogInterface{

    public MyAlerDialog(Context context) {
        super(context);
    }

}
