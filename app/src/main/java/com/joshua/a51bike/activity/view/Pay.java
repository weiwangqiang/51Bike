package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.pay.util.PayUtils;
import com.joshua.a51bike.util.AppUtil;

import org.xutils.view.annotation.ContentView;

/**
 * Created by wangqiang on 2017/1/9.
 */
@ContentView(R.layout.pay)
public class Pay extends BaseActivity {
    private String url = AppUtil.BaseUrl +"/user/insertCharge";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    public void init() {
        initActionBar();
        setLister();
    }
    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    private void setLister() {
        findViewById(R.id.pay_button).setOnClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void pay(){
        uiUtils.showToast("正在跳转");
        PayUtils.payV2(this);

    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_button:
                pay();
                break;
        }
    }
}