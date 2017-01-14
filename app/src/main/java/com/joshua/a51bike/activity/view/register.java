package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.view.View;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;

import org.xutils.view.annotation.ContentView;

/**
 * class description here
 *  注册
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */
@ContentView(R.layout.login_fast)
public class register extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        findId();
        setLister();
    }

    public void findId() {

    }

    public void setLister() {
//        findViewById(R.id.left_back).setOnClickListener(this);
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
            default:
                break;
        }
    }
}