package com.joshua.a51bike.activity.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;

import org.xutils.view.annotation.ContentView;

/**
 * class description here
 *  更改手机验证
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-12
 */
@ContentView(R.layout.user_infor_pb)
public class UserInforPhoneBefor extends BaseActivity {
    private static final String TAG = "UserInforPhoneBefor";

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
    private EditText phone,code;
    public void findId() {
        phone = (EditText) findViewById(R.id.bp_phone_number);
        code = (EditText) findViewById(R.id.bp_code);
    }


    public void setLister() {
        findViewById(R.id.bp_get_code).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bp_get_code:
                getCode();
                break;
            default:
                break;
        }
    }
    /*获取验证码*/
    private void getCode() {
        String s = "";
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

}
  
  
  
  
