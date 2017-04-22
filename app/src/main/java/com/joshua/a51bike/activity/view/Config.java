package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.joshua.a51bike.Interface.DialogCallBack;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.LogoutState;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.CurrencyAlerDialog;

import org.xutils.view.annotation.ContentView;

/**
 * class description here
 *
 *  设置界面
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */
@ContentView(R.layout.config)
public class Config extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        initActionBar();
        findId();
        setLister();
    }

    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    public void findId() {

    }

    public void setLister() {
        findViewById(R.id.Logout).setOnClickListener(this);
        findViewById(R.id.config_about).setOnClickListener(this);
        findViewById(R.id.config_update).setOnClickListener(this);
        findViewById(R.id.config_suggest).setOnClickListener(this);

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
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Logout:
                logout();
                break;
            case R.id.config_update:
                uiUtils.showToast("已经是最新的了");
                break;
            case R.id.config_suggest:
                userControl.configSuggest(Config.this);
                break;
            case R.id.config_about:
                userControl.configAbout(Config.this);
                break;
            default:
                break;
        }
    }
    /**
     * 退出
     */
    private void logout() {

        dialogControl.setDialog(new CurrencyAlerDialog(Config.this,"温馨提示",
                "确定要离开我吗？",new callback()));
        dialogControl.show();

    }
    public class callback implements DialogCallBack {

        @Override
        public void sure() {
            userControl.setUserState(new LogoutState());
            userControl.setUser(null);
            uiUtils.showToast("注销成功！");
        }

        @Override
        public void cancel() {

        }
    }
}