package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.MainActivity;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.adapter.FirstVPAdapter;
import com.joshua.a51bike.adapter.mytransformer;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-28
 */
@ContentView(R.layout.first_click)
public class FirstClick extends BaseActivity {
    private static final String TAG = "FirstClick";
    private List<View> pagerList ;

    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;

    @ViewInject(R.id.first_start)
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        findId();
        setLister();
        initViewPager();
    }

    public void findId() {

    }

    public void setLister() {
        start.setOnClickListener(this);
    }

    public void initViewPager(){

        pagerList =  new ArrayList<>();
        pagerList.add(uiUtils.inflate(R.layout.first_click_item));
        pagerList.add(uiUtils.inflate(R.layout.first_click_item));
        pagerList.add(uiUtils.inflate(R.layout.first_click_item));
        pagerList.add(uiUtils.inflate(R.layout.first_click_item));
        viewPager.setAdapter(new FirstVPAdapter(pagerList));
        viewPager.addOnPageChangeListener(new myPagerChanger());
        viewPager.setPageTransformer(true,new mytransformer());

    }

    private class myPagerChanger implements ViewPager.OnPageChangeListener{


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(position ==3 )
                start.setVisibility(View.VISIBLE);
            else
                start.setVisibility(View.GONE);
        }


        @Override
        public void onPageSelected(int position) {

        }


        @Override
        public void onPageScrollStateChanged(int state) {

        }
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
            case R.id.first_start:
                startActivity(new Intent(this, MainActivity.class));
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
}