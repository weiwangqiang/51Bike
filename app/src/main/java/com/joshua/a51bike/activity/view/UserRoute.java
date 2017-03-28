package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joshua.a51bike.Interface.myitemLister;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.adapter.URRecyclerAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @ViewInject(R.id.route_List)
    private RecyclerView recyclerView;
    private List<HashMap<String,String>> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Object[] o = new Object[1];
    }

    public void init() {
        setLister();
        initListView();
    }

    private void initListView() {
        addData();
        LinearLayoutManager   manager =  new LinearLayoutManager(UserRoute.this);
        recyclerView.setLayoutManager(manager);

        URRecyclerAdapter adapter = new URRecyclerAdapter(UserRoute.this,
                R.layout.user_route_listitem,data);
        adapter.setLister(new myitemLister() {
            @Override
            public void ItemLister(int position) {
                Intent intent = new Intent(UserRoute.this,UserRouteMes.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void addData() {
        HashMap<String,String> map = new HashMap<>();
        map.put("time","2016-121-123 23:32");
        map.put("spend","￥ 1.1");
        map.put("carId","6565656");
        data.add(map);
        data.add(map);
        data.add(map);
        data.add(map);
        data.add(map);
        data.add(map);
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