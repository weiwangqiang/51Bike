package com.joshua.a51bike.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * class description here
 *
 *  第一次进入app 界面 的viewAdapter
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-11
 */
public class FirstVPAdapter extends PagerAdapter {
    public List<View> mListViews;

    public FirstVPAdapter(List<View> mListViews) {
        this.mListViews = mListViews;
    }
    /**
     * 销毁预加载以外的view对象, 会把需要销毁的对象的索引位置传进来就是position
     */
    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        arg0.removeView(mListViews.get(arg1));
    }

    @Override
    public int getCount() {
        return mListViews.size();
//        return Integer.MAX_VALUE;
    }
    /**
     * 创建一个view
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        View view = mListViews.get(position % 5);
//        ((ViewPager)arg0).addView(view, 0);
//        return view;

        container.addView(mListViews.get(position), 0);
        return mListViews.get(position);
    }
    /**
     * 判断出去的view是否等于进来的view 如果为true直接复用
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
