package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.MainActivity;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.adapter.FirstVPAdapter;
import com.joshua.a51bike.adapter.mytransformer;
import com.joshua.a51bike.util.PrefUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * class description here
 *
 *  第一次进入App时的引导界面
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
    private Animation fromBot,fromTop;

    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;

    private Button start;
    private TextView textView1,textView2;
    @ViewInject(R.id.first_Radio1)
    private RadioButton mRadioB1;

    @ViewInject(R.id.first_Radio2)
    private RadioButton mRadioB2;

    @ViewInject(R.id.first_Radio3)
    private RadioButton mRadioB3;

    @ViewInject(R.id.first_radioGroup)
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        fromBot = AnimationUtils.loadAnimation(FirstClick.this, R.anim.in_from_bottom_to_top);
        fromTop = AnimationUtils.loadAnimation(FirstClick.this,R.anim.in_from_top_set);
        findId();
        initViewPager();
        setLister();
    }

    public void findId() {

    }

    public void setLister() {
        start.setOnClickListener(this);
    }

    public void initViewPager(){
        int id[] = new int[]{R.drawable.first1,R.drawable.first3,R.drawable.first2};
        int res[] = new int[]{R.layout.first_click_item1,
                R.layout.first_click_item2,R.layout.first_click_item3};
        pagerList =  new ArrayList<>();

        View view1 = uiUtils.inflate(res[0]);
        View view2 = uiUtils.inflate(res[1]);
        View view3 = uiUtils.inflate(res[2]);
        view1.findViewById(R.id.first_item_image).setBackgroundResource(id[0]);
        view2.findViewById(R.id.first_item_image).setBackgroundResource(id[2]);
        view3.findViewById(R.id.first_item_image).setBackgroundResource(id[1]);
        start = (Button) view3.findViewById(R.id.first_start);
        textView1 = (TextView) view1.findViewById(R.id.TopTextView);
        textView2 = (TextView) view2.findViewById(R.id.TopTextView);
        pagerList.add(view1);
        pagerList.add(view2);
        pagerList.add(view3);
        viewPager.setAdapter(new FirstVPAdapter(pagerList));
        viewPager.addOnPageChangeListener(new myPagerChanger());
        viewPager.setPageTransformer(true,new mytransformer());
        //设置文字的大小
        String str1 = "实时查看车的状态";
        String str2 = "随时随地享受骑车的\n乐趣";
        SpannableString spanStr = new SpannableString(str1);
          spanStr.setSpan(new AbsoluteSizeSpan(50), str1.indexOf("实"), str1.indexOf("时")+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView1.setText(spanStr);
        spanStr = new SpannableString(str2);
        spanStr.setSpan(new AbsoluteSizeSpan(50), str2.indexOf("乐"), str2.indexOf("趣")+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView2.setText(spanStr);
        //沉浸式
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    private class myPagerChanger implements ViewPager.OnPageChangeListener{


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            Log.i(TAG, "onPageScrolled: ");
        }


        @Override
        public void onPageSelected(int position) {
            Log.i(TAG, "onPageSelected: "+position);
            showRadioButton(position);
        }


        @Override
        public void onPageScrollStateChanged(int state) {
            Log.i(TAG, "onPageScrollStateChanged: "+state);
        }
    }

    public void showRadioButton(int position){
        textView1.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        start.setVisibility(View.INVISIBLE);
        switch (position){
            case 0:
                textView1.startAnimation(fromTop);
                textView1.setVisibility(View.VISIBLE);
                mRadioB1.setChecked(true);
                break;
            case 1:
                textView2.startAnimation(fromTop);
                textView2.setVisibility(View.VISIBLE);
                mRadioB2.setChecked(true);
                break;
            case 2:
                start.startAnimation(fromBot);
                start.setVisibility(View.VISIBLE);
                start.setClickable(true);
                mRadioB3.setChecked(true);
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
                PrefUtils.setBoolean(this,"isFirst",false);
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