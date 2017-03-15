package com.joshua.a51bike.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by wangqiang on 2016/5/15.主界面的Viewpager
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    public List<Fragment> list;

    public FragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position % 2 == 0) {
            return "消费明细";
        } else {
            return "充值明细";
        }
    }
}
