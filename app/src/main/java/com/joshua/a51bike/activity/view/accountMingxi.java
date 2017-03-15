package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.fragments.DetailFragment;
import com.joshua.a51bike.adapter.FragmentAdapter;

import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;
import java.util.List;

/**
 * class description here
 *  余额明细
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-07
 */
@ContentView(R.layout.account_mingxi)
public class accountMingxi extends BaseActivity {
    private static final String TAG = "accountMingxi";
    private TabLayout tabLayout;
    private List<Fragment> list = new ArrayList<>();
    private FragmentAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        initActionBar();
        findId();
        initTabLayout();
        setLister();
    }

    private void initTabLayout() {
        initViewPager();
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for(int i = 0;i<2;i++){
            TabLayout.Tab tab = tabLayout.newTab();
            tabLayout.addTab(tab);
        }
        tabLayout.setupWithViewPager(viewPager);
    }
    private void initViewPager(){
        list.add(new DetailFragment());
        list.add(new DetailFragment());
        //刚开始只会加载前两个fragment
        adapter  = new FragmentAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit();
//        viewPager.addOnPageChangeListener(new pagerlist());
        viewPager.setCurrentItem(0);
    }

    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void findId() {
        viewPager = (ViewPager) findViewById(R.id.mingXi_viewPager);
    }

    public void setLister() {

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
    }
}
  
  
  
  
