package com.joshua.a51bike.activity.dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.joshua.a51bike.Interface.DialogCallBack;
import com.joshua.a51bike.Interface.MyAlerDialog;
import com.joshua.a51bike.R;

/**
 * 首界面的引导界面dialog
 * Created by wangqiang on 2017/1/9.
 */

public class MainFirstAlerDialog extends MyAlerDialog implements View.OnClickListener{
    String TAG ="MarginAlerDialog";
    private TextView sure;
    private DialogCallBack callBack;
    private Activity context;
    public MainFirstAlerDialog(Activity context, DialogCallBack callBack) {
        super(context);
        this.context = context;
        this.callBack = callBack;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_open_ble);
        getWindow().setBackgroundDrawable(new ColorDrawable());

         sure = (TextView) findViewById(R.id.dialog_sure);
        setCanceledOnTouchOutside(false);
        sure.setOnClickListener(this);
    }
    @Override
    protected  void onStart(){
        super.onStart();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_sure:
                Log.i(TAG, "onClick: dialog_sure ");
                cancel();
                callBack.sure();
                break;
        }
    }

    @Override
    public void myShow() {
        Log.i(TAG, "myShow: myShow");
        show();
    }

    @Override
    public void myCancel() {
        Log.i(TAG, "myCancel: myCancel");
        cancel();
    }
}
