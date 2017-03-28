package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.entity.school.Province;
import com.joshua.a51bike.entity.school.ProvinceList;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-28
 */
@ContentView(R.layout.choice_province)
public class ChoiceProvince extends BaseActivity {
    private static final String TAG = "ChoiceProvince";

    @ViewInject(R.id.choice_listView)
    private ListView listView;

    private SimpleAdapter adapter;
    private List<Map<String,String>> list = new ArrayList<>();
    private final int GET_PRO = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        initActionBar();
        findId();
        setLister();
        RequestParams params = new RequestParams("http://www.hisihi.com/app.php?s=/school/province");
        params.addHeader("Content-Type","application/json");
        post(params);
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

    }

    private   Callback.Cancelable cancelable;

    private void post(RequestParams params){
        Log.i(TAG, "post:");
        cancelable =  x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result is ");
                addDataToList(result);
                initList();
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

    private void addDataToList(String result) {
        Log.i(TAG, "addDataToList: ");
        Province p = new Gson().fromJson(result, Province.class);
        for(ProvinceList pro : p.getData()){
            Map<String,String> map = new HashMap<>();
            map.put("name",pro.getProvince_name());
            map.put("id",pro.getProvince_id());
            list.add(map);
            Log.i(TAG, "onSuccess: pro name "+pro.getProvince_name());
        }
    }

    public void initList(){
        Log.i(TAG, "initList: ");
        adapter = new SimpleAdapter(this,
                list,R.layout.choice_province_item,
                new String[]{"name"},new int[]{R.id.choice_item_text});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map = list.get(position);
                String pro_id = map.get("id");
                Intent intent = new Intent(ChoiceProvince.this,ChoiceSchool.class);
                intent.putExtra("id",pro_id);
                startActivityForResult(intent, GET_PRO);
            }
        });
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
        if(requestCode == GET_PRO){
            if(resultCode == 110){
                setResult(110,data);
                finish();
            }
        }
    }
}
  
  
  
  
