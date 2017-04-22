package com.joshua.a51bike.activity.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.entity.school.School;
import com.joshua.a51bike.entity.school.SchoolList;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * class description here
 *
 *  搜索车牌号界面，效果与扫码一样的
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-28
 */
@ContentView(R.layout.search_bike)
public class searchBike extends BaseActivity {
    private static final String TAG = "searchBike";
    private StringBuffer sb = new StringBuffer();

    @ViewInject(R.id.search_input_edit)
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        initActionBar();
        setLister();
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
    }

    private   Callback.Cancelable cancelable;

    private void post(RequestParams params){
        Log.i(TAG, "post:");
        cancelable =  x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result is ");
                School p = new Gson().fromJson(result, School.class);
                for(SchoolList pro : p.getData()){
                    Log.i(TAG, "onSuccess: pro name "+pro.getSchool_name());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                uiUtils.showToast("失败！");
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


    public void setLister() {
        findViewById(R.id.search_button).setOnClickListener(this);
        findViewById(R.id.one).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.three).setOnClickListener(this);
        findViewById(R.id.four).setOnClickListener(this);
        findViewById(R.id.five).setOnClickListener(this);
        findViewById(R.id.six).setOnClickListener(this);
        findViewById(R.id.seven).setOnClickListener(this);
        findViewById(R.id.eight).setOnClickListener(this);
        findViewById(R.id.nine).setOnClickListener(this);
        findViewById(R.id.zero).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        findViewById(R.id.sure).setOnClickListener(this);
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
            case R.id.one:
                sb.append("1");
                updataEditView();

                break;

            case R.id.two:
                sb.append("2");
                updataEditView();
                break;

            case R.id.three:
                sb.append("3");
                updataEditView();
                break;

            case R.id.four:
                sb.append("4");
                updataEditView();
                break;
            case R.id.five:
                sb.append("5");
                updataEditView();
                break;

            case R.id.six:
                sb.append("6");
                updataEditView();
                break;

            case R.id.seven:
                sb.append("7");
                updataEditView();
                break;

            case R.id.eight:
                sb.append("8");
                updataEditView();
                break;

            case R.id.nine:
                sb.append("9");
                updataEditView();

                break;

            case R.id.zero:
                sb.append("0");
                updataEditView();
                break;

            case R.id.delete:
                if(sb.length() == 0 ) break;
                sb.delete(sb.length()-1,sb.length());
                updataEditView();
                break;

            case R.id.sure:
                if(sb.length() < 2 )
                    break;
                userControl.toBikeMes(this,"  ");

//                RequestParams params = new RequestParams();
//                params.addHeader("Content-Type","application/json");
//                post(params);
                break;
            case R.id.search_button:
                if(sb.length() < 2 )
                    break;
                userControl.toBikeMes(this,"  ");

                break;
            default:
                break;
        }
    }

    private void updataEditView() {
        editText.setText(sb.toString());
        editText.setSelection(sb.length());
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
  
  
  
  
