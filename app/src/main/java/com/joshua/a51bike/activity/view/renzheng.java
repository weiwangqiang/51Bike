package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.MyTools;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-13
 */
@ContentView(R.layout.renzheng)
public class renzheng extends BaseActivity {
    private String TAG = "renzheng" ;
    private User user;
    private String url = AppUtil.BaseUrl + "/user/updateUser";
    private EditText getName, getId,getSchool;
    private final int GET_PRO = 100;

    @ViewInject(R.id.get_pro)
    private TextView getPro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        userControl = UserControl.getUserControl();
        findId();
        setLister();
        initActionBar();
    }
    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    public void findId() {
        getName = (EditText) findViewById(R.id.name);
        getId = (EditText) findViewById(R.id.id);
        getSchool = (EditText) findViewById(R.id.school);
//        getName.setText(userControl.getUser().getUsername());
//        getId.setText("22");
//        getSchool.setText("hahaha");
    }

    public void setLister() {
        findViewById(R.id.upDate).setOnClickListener(this);
        getPro.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upDate:
                update();
                break;
            case R.id.get_pro:
                  startActivityForResult(new Intent(this,ChoiceProvince.class),GET_PRO);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_PRO){
            if(resultCode == 110){
                Log.i(TAG, "onActivityResult: "+data.getStringExtra("name"));
                getSchool.setText(data.getStringExtra("name"));
            }
        }
    }
    private void update(){
        if (MyTools.EditTextIsNull(getName)
                || MyTools.EditTextIsNull(getId)
                || MyTools.EditTextIsNull(getSchool)) {
            uiUtils.showToast("不能为空");
            return;
        }
        String name = getName.getText().toString();
        String id = getId.getText().toString();
        String school = getSchool.getText().toString();

        RequestParams params = new RequestParams(url);
        params.addBodyParameter("username",name);
//        params.addBodyParameter("userpass",id);
        params.addBodyParameter("userstate",id);

        user = new User();
        user.setUsername(name);
        user.setUserpass(id);
        user.setUserstate(1);
        post(params);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
    }

    private void post(RequestParams params){
        Log.d(TAG, "post: post by xutils------>>");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "success: object is "+ result.toString());
                dialogControl.cancel();

                if(result.equals("ok")){
                    uiUtils.showToast("修改成功！");
                    userControl.setUser(user);
                    finish();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                uiUtils.showToast("修改失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
    }


}