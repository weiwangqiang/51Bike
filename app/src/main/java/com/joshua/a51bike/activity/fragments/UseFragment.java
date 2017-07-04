package com.joshua.a51bike.activity.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.joshua.a51bike.Interface.myitemLister;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.adapter.TimestampTypeAdapter;
import com.joshua.a51bike.adapter.UseRecyclerAdapter;
import com.joshua.a51bike.animator.MyItemAnimator;
import com.joshua.a51bike.entity.UserAndUse;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * class description here
 *
 *  消费明细
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-15
 */
public class UseFragment extends Fragment {
    private String TAG = "DetailSpend";
    private SwipeRefreshLayout swipeRefreshLayout;

    public static UseFragment f;
    private View mRootView;
    private String url  ;
    private String from  ;
    private UserControl userControl = UserControl.getUserControl();
    private RecyclerView mRecyclerView;
    private UseRecyclerAdapter adapter;
    private TextView error_TextView;

    public static UseFragment newInstance(String url, String from) {
        f = new UseFragment();
        Bundle b = new Bundle();
        b.putString("url",url);
//        b.putString("TAG",TAG);
        b.putString("from",from);
        f.setArguments(b);
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_detail,container,false);
        error_TextView = (TextView) mRootView.findViewById(R.id.error_TextView);
        initRefresh();
        initView();
        initParams();
        return mRootView;
    }

    private void initParams() {
        Bundle b = getArguments();
        url = b.getString("url");
        from = b.getString("from");
    }

    public void initRefresh(){
        swipeRefreshLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.MEASURED_STATE_TOO_SMALL);
        swipeRefreshLayout.setDistanceToTriggerSync(200);
        swipeRefreshLayout.setOverScrollMode( View.OVER_SCROLL_NEVER );
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(getActivity(),R.color.white));
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),R.color.baseColor),
                ContextCompat.getColor(getActivity(),R.color.baseColor));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                buildParam();
            }
        });
    }

    public void buildParam(){
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("uid",userControl.getUser().getUserid()+"");
        Log.i(TAG, "buildParam: ============== "+params.toString());
        post(params);
    }
    /*发送请求*/
    private  void post(RequestParams params){
        Log.i(TAG, "post: "+params.toString());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: ===== "+result);
                swipeRefreshLayout.setRefreshing(false);
                addDate(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                swipeRefreshLayout.setRefreshing(false);
                isEmptyDate();
                ex.printStackTrace();
                Log.i(TAG, "onError: ");
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
    private List<UserAndUse> data = new ArrayList<>() ;
    private void addDate(String msg) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
        gsonBuilder.registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter());
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<ArrayList<UserAndUse>>() {}.getType();
        List<UserAndUse> d  =gson.fromJson(msg, type);
        data.addAll(d);
        adapter.RefreshDate(data);
        isEmptyDate();
    }

    /**
     * 判断是否为空
     */
    private void isEmptyDate() {
        if(adapter.getItemCount() == 0)
            error_TextView.setVisibility(View.VISIBLE);
        else
            error_TextView.setVisibility(View.GONE);
    }

    private LinearLayoutManager manager;
    private Map<String ,String> map;
    private void initView() {
        adapter = new UseRecyclerAdapter(getActivity(),
                R.layout.fragment_detail_listitem,data,from);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.detail_recyclerView);
        manager =  new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new MyItemAnimator());
        adapter.setItemClickListener(new myitemLister() {
            @Override
            public void onItemClicked(int position) {
                Log.i(TAG, "onItemClicked: "+position);
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
        if(swipeRefreshLayout!=null){
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    buildParam();
                }
            });
        }
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onHiddenChanged (boolean hidden){

    }
}
