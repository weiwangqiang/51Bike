package com.joshua.a51bike.activity.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joshua.a51bike.Interface.myitemLister;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.adapter.DetailRecyclerAdapter;
import com.joshua.a51bike.animator.MyItemAnimator;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
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
public class DetailFragment extends Fragment {
    private String TAG = "DetailSpend";
    private View mRootView;
    private String url  = AppUtil.BaseUrl+"/user/getChargesByid";
    private UserControl userControl = UserControl.getUserControl();
    private RecyclerView mRecyclerView;
    public List<Map<String,String>> date = new ArrayList<>();
    private DetailRecyclerAdapter adapter;
    private TextView error_TextView;

    public static DetailFragment newInstance(String url,String TAG,int from) {
        DetailFragment f = new DetailFragment();
        Bundle b = new Bundle();
        b.putString("url",url);
        b.putString("TAG",TAG);
        b.putInt("from",from);
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
//        initRefresh();
        initView();
        getData();
        return mRootView;
    }
    public void getData(){
        RequestParams params = new RequestParams(url);
        User user = UserControl.getUserControl().getUser();
        params.addBodyParameter("userid",userControl.getUser().getUserid()+"");
        post(params);
    }
    /*发送请求*/
    private  void post(RequestParams params){
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result is "+result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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
    private LinearLayoutManager manager;
    private Map<String ,String> map;
    private void initView() {
        adapter = new DetailRecyclerAdapter(getActivity(),
                R.layout.fragment_detail_listitem,date,
                new String[]{"title","time","money"});

        map = new HashMap<>();
        map.put("title","充值记录");
        map.put("time","2016-02-12 :21:13:12");
        map.put("money","+100元");

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
        adapter.RefreshDate(map);
        adapter.RefreshDate(map);
        mRecyclerView.scrollToPosition(0);
        if(adapter.getItemCount() == 0)
            error_TextView.setVisibility(View.VISIBLE);
        else
            error_TextView.setVisibility(View.GONE);
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
