package com.joshua.a51bike.activity.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.joshua.a51bike.Interface.MyProgress;
import com.joshua.a51bike.R;

/**
 * Created by wangqiang on 2017/1/9.
 */

public class LocateProgress extends MyProgress  {
        private  Context context;
    private String content;
    public LocateProgress(Context context,String content) {
        super(context);
        this.context = context;
        this.content = content;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_black);
        getWindow().setBackgroundDrawable(new ColorDrawable());

        setIndeterminate(false);
        setCancelable(true);
    }


    @Override
    public void myShow() {
        show();
    }

    @Override
    public void myCancel() {
        cancel();
    }
}
