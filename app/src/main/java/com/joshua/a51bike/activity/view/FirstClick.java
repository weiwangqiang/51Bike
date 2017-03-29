package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.MainActivity;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.adapter.FirstVPAdapter;

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

    @ViewInject(R.id.first_Radio1)
    private RadioButton mRadioB1;

    @ViewInject(R.id.first_Radio2)
    private RadioButton mRadioB2;

    @ViewInject(R.id.first_radioGroup)
    private RadioGroup mRadioGroup;

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
        View view1 = uiUtils.inflate(R.layout.first_click_item);
        View view2 = uiUtils.inflate(R.layout.first_click_item);
        View view3 = uiUtils.inflate(R.layout.first_click_item);
        view1.findViewById(R.id.first_item_image).setBackgroundResource(R.drawable.first1);
        view2.findViewById(R.id.first_item_image).setBackgroundResource(R.drawable.first2);
        view3.findViewById(R.id.first_item_image).setBackgroundResource(R.drawable.first3);
        pagerList.add(view1);
        pagerList.add(view2);
        pagerList.add(view3);
        viewPager.setAdapter(new FirstVPAdapter(pagerList));
        viewPager.addOnPageChangeListener(new myPagerChanger());
//        viewPager.setPageTransformer(true,new mytransformer());

    }

    private class myPagerChanger implements ViewPager.OnPageChangeListener{


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }


        @Override
        public void onPageSelected(int position) {
            showRadioButton(position);
            if(position ==2 )
                start.setVisibility(View.VISIBLE);
            else
                start.setVisibility(View.GONE);
        }


        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public void showRadioButton(int position){
        if(position == 2){
            mRadioGroup.setVisibility(View.GONE);
            return ;
        }
        if(mRadioGroup.getVisibility() == View.GONE)
            mRadioGroup.setVisibility(View.VISIBLE);
        switch (position){
            case 0:
                mRadioB1.setChecked(true);
                break;
            case 1:
                mRadioB2.setChecked(true);
                break;
            case 2:
                break;

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