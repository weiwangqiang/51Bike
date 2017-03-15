package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;

import org.xutils.view.annotation.ContentView;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-07
 */
@ContentView(R.layout.account)
public class account extends BaseActivity {
    private static final String TAG = "account";

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
        Toolbar myToolbar = (Toolbar)findViewById(R.id.account_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    public void findId() {

    }

    public void setLister() {
        findViewById(R.id.account_minxi).setOnClickListener(this);

        findViewById(R.id.account_recharge).setOnClickListener(this);
        findViewById(R.id.account_yajin).setOnClickListener(this);

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
            case R.id.account_recharge:
                userControl.accountRecharge(account.this);
                break;
            case R.id.account_minxi:
                userControl.accountMiXi(account.this);
                break;
            case R.id.account_yajin:
                userControl.accountYaJin(account.this);
                break;
            default:
                break;
        }
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
    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "onStop: tiem is "+System.currentTimeMillis());
    }
}
  
  
  
  
