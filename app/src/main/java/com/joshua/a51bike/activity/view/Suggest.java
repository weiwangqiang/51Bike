package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
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
 * @since 2017-03-07
 */
@ContentView(R.layout.suggest)
public class Suggest extends BaseActivity {
    private static final String TAG = "Suggest";

    private String url  = AppUtil.BaseUrl+"/user/insertAdvice";
    @ViewInject(R.id.get_suggest)
    private EditText getSuggest;

    @ViewInject(R.id.submit)
    private Button  submit;
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
        submit.setOnClickListener(this);
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
            case R.id.submit:
              submit();
                break;
            default:
                break;
        }
    }
    public void submit(){
        if(MyTools.EditTextIsNull(getSuggest)){
            uiUtils.showToast("不能为空！");
            return;
        }
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
        String content = getSuggest.getText().toString();
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("content",content);
        params.addBodyParameter("userid",userControl.getUser().getUserid()+"");
        post(params);
    }

    private   Callback.Cancelable cancelable;
    private void post(RequestParams params){
        cancelable =  x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                uiUtils.showToast("感谢您的建议！");
                getSuggest.setText("");
                dialogControl.cancel();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                uiUtils.showToast("失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                dialogControl.cancel();
            }

            @Override
            public void onFinished() {

            }
        });
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
     * Called when an activity you launched exits
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
  
  
  
  
