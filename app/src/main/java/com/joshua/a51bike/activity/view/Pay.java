package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.view.View;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.util.AppUtil;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.xutils.view.annotation.ContentView;

/**
 * Created by wangqiang on 2017/1/9.
 */
@ContentView(R.layout.pay)
@RunWith(RobolectricTestRunner.class)
public class Pay extends BaseActivity {
    private String url = AppUtil.BaseUrl +"/user/insertCharge";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    private void init() {
        setLister();
    }

    private void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);
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
        }
    }
}