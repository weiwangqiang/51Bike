package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.LogoutState;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;

import org.xutils.view.annotation.ContentView;

/**
 * class description here
 *  设置
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */
@ContentView(R.layout.config)
public class Config extends BaseActivity {
    private UserControl userControl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        userControl = UserControl.getUserControl();
        findId();
        setLister();
    }

    public void findId() {

    }

    public void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);

        findViewById(R.id.Logout).setOnClickListener(this);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
                break;
            case R.id.Logout:
                userControl.setUserState(new LogoutState());
                Toast.makeText(this, "注销成功！", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}