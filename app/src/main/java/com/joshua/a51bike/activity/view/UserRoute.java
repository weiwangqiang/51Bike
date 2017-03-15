package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;

import org.xutils.view.annotation.ContentView;

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
 * @since 2017-03-01
 */
@ContentView(R.layout.user_route)
public class UserRoute extends BaseActivity implements ListView.OnItemClickListener{
    private static final String TAG = "UserRoute";
    private ListView listView;
    private List<Map<String,String>> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Object[] o = new Object[1];
    }

    public void init() {
        findId();
        setLister();
        initListView();
    }

    private void initListView() {
        addData();
        listView.setAdapter(new SimpleAdapter(UserRoute.this,data,R.layout.user_route_listitem
       , new String[]{"time","spend","carId"}
                ,new int[]{R.id.route_item_time,R.id.route_item_spend,R.id.route_item_carId}));
        listView.setOnItemClickListener(this);
    }

    private void addData() {
        Map<String,String> map = new HashMap<>();
        map.put("time","2016-121-123 23；32");
        map.put("spend","￥ 1.1");
        map.put("carId","6565656");
        data.add(map);
        data.add(map);
        data.add(map);
        data.add(map);
    }

    public void findId() {
       listView = (ListView) findViewById(R.id.route_List);
    }

    public void setLister() {
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
            default:
                break;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: "+position);
        Intent intent = new Intent(this,UserRouteMes.class);
        startActivity(intent);
    }
}