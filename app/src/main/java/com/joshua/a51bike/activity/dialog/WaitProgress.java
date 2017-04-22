package com.joshua.a51bike.activity.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.joshua.a51bike.Interface.MyProgress;
import com.joshua.a51bike.R;

/**
 * 等待对话框 就一个progress
 *
 * Created by wangqiang on 2017/1/9.
 */

public class WaitProgress extends MyProgress  {
        private  Context context;
    private String content;
    public WaitProgress(Context context) {
        super(context);
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wait);
        getWindow().setBackgroundDrawable(new ColorDrawable());
        setCanceledOnTouchOutside(false);

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
