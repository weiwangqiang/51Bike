package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joshua.a51bike.Interface.myitemLister;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.adapter.TimestampTypeAdapter;
import com.joshua.a51bike.adapter.URRecyclerAdapter;
import com.joshua.a51bike.entity.UserAndUse;
import com.joshua.a51bike.util.AppUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * class description here
 *
 * 用户的历史租车轨迹列表界面
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-01
 */
@ContentView(R.layout.user_route)
public class UserRoute extends BaseActivity implements ListView.OnItemClickListener{
    private static final String TAG = "UserRoute";
    private   String url = AppUtil.BaseUrl+"/car/getList";//uid

    @ViewInject(R.id.route_List)
    private RecyclerView recyclerView;
    private List<HashMap<String,String>> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        setLister();
        getData();
    }
    public void getData(){
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("uid",userControl.getUser().getUserid()+"");
        post(params);
    }
    /*发送请求*/
    private  void post(RequestParams params){
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: -------------------");
                Log.i(TAG, "onSuccess: "+result);
                parseResult(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: --------------------");
                ex.printStackTrace();
//              1handler.sendEmptyMessage(NET_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled: cancel", null);

            }

            @Override
            public void onFinished() {

            }
        });
    }
    private List<UserAndUse> list;
    private  void parseResult(String result) {
        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
        gsonBuilder.setDateFormat("mm:ss");
        gsonBuilder.registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter());
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<ArrayList<UserAndUse>>() {}.getType();
        list =gson.fromJson(result, type);
        for(UserAndUse use :list){
            Log.i(TAG, "parseResult: "+use.getUseDistance()
                    +" time  "+use.getUseHour());
        }
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
            public void onItemClicked(int position) {
                Intent intent = new Intent(UserRoute.this,UserRouteMes.class);
                intent.putExtra("id",data.get(position).get("id"));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void addData() {
        DateFormat format = new SimpleDateFormat("mm:ss");

        for(UserAndUse use:list ){
            if(use == null)
                break;
            HashMap<String,String> map = new HashMap<>();
          if( use.getUseHour() !=null)
              map.put("time","使用时长："+format.format(new Date(use.getUseHour().getTime())));
            map.put("spend",use.getUseMoney()+"");
            map.put("carId","车牌号"+use.getCarId()+"");
            map.put("id",use.getId()+"");
            data.add(map);
        }

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