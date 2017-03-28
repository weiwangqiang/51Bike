package com.joshua.a51bike.activity.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;

import com.joshua.a51bike.Interface.MyAlerDialog;
import com.joshua.a51bike.R;

/**
 * Created by wangqiang on 2017/1/9.
 */

public class GPSAlerDialog extends MyAlerDialog implements View.OnClickListener{
    private Activity context;
    public GPSAlerDialog(Activity context) {
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // must be called before set content
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gps);
        getWindow().setBackgroundDrawable(new ColorDrawable());
        findViewById(R.id.dialog_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_sure).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_cancel:
                cancel();
                break;
            case R.id.dialog_sure:
                cancel();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivityForResult(intent,1); // 设置完成后返回到原来的界面
                break;
        }
    }

    @Override
    public void myShow() {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.hardware.location.gps","com.joshua.a51bike"));
        System.out.print("GPS ----- permission --------------- "+permission);
        if (permission && (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))) {
            show();
        }
    }

    @Override
    public void myCancel() {
        cancel();
    }
}
