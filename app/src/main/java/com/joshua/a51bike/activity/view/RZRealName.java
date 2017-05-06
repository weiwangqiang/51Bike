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
import com.joshua.a51bike.customview.progress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.MyTools;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * class description here
 *
 *  用户身份认证
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-13
 */
@ContentView(R.layout.rz_realname)
public class RZRealName extends BaseActivity {
    private String TAG = "renzheng" ;
    private User user;
    private String url = AppUtil.BaseUrl + "/user/upuserin";
    private EditText getName, getId;
    private final int GET_PRO = 100;

    @ViewInject(R.id.main_progress_view)
    private progress useProgress;

    @ViewInject(R.id.main_progress)
    private View useProgressParent;

    private TextView textView1,textView2,textView3,textView4;
    private List<TextView> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setProgressView();
    }
    private void setProgressView() {

        useProgress.setPoistion(4);
        int i ;
        for(TextView T:list){
            T.setTextColor(getResources().getColor(R.color.gray));
        }
        for(i = 0;i<4;i++){
            list.get(i).setTextColor(getResources().getColor(R.color.baseColor));
        }
    }
    public void init() {
        userControl = UserControl.getUserControl();
        findId();
        setLister();
        initActionBar();
        showProgreesOrNot(userControl.getUser());

    }

    private void showProgreesOrNot(User user) {
        setProgressView(1);//更新
        if(user == null ){
            useProgressParent.setVisibility(View.GONE);
            return;
        }
        if(useProgressParent.getVisibility() == View.GONE)
            useProgressParent.setVisibility(View.VISIBLE);
        if(user.getRealName() == null )
            setProgressView(1);
        if(user.getUsermoney() != 0)
            setProgressView(4);

    }

    private void setProgressView(int a) {

        useProgress.setPoistion(a);
        int i ;
        for(TextView T:list){
            T.setTextColor(getResources().getColor(R.color.gray));
        }
        for(i = 0;i<a;i++){
            list.get(i).setTextColor(getResources().getColor(R.color.baseColor));
        }
    }

    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    public void findId() {
        getName = (EditText) findViewById(R.id.renzheng_name);
        getId = (EditText) findViewById(R.id.renzheng_id);

        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView3 = (TextView) findViewById(R.id.text3);
        textView4 = (TextView) findViewById(R.id.text4);

        list.add(textView1);
        list.add(textView2);
        list.add(textView3);
        list.add(textView4);
//        getName.setText(userControl.getUser().getUsername());
//        getId.setText("22");
//        getSchool.setText("hahaha");
    }

    public void setLister() {
        findViewById(R.id.rz_realName_submit).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rz_realName_submit:
                update();
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

    }
    private void update(){
        if (MyTools.EditTextIsNull(getName)
                || MyTools.EditTextIsNull(getId)) {
            uiUtils.showToast("不能为空");
            return;
        }

        String realName = getName.getText().toString();
        String userNumber = getId.getText().toString();
        if(userNumber.length()!= 18){
            uiUtils.showToast("身份证不正确");
            return ;
        }
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("userid",userControl.getUser().getUserid()+"");
        params.addBodyParameter("realName",realName);
        params.addBodyParameter("userNumber",userNumber);
        post(params);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
    }

    private void post(RequestParams params){
        Log.i(TAG, "post: params"+params.toString());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "success: object is "+ result.toString());
                dialogControl.cancel();
                if(result.equals("ok")){
                    uiUtils.showToast("修改成功！");
                    userControl.getUser().setRealName(getName.getText().toString());
                    userControl.getUser().setUserNumber(getId.getText().toString());
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