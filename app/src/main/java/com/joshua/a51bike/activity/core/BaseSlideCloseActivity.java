package com.joshua.a51bike.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewGroup;

import com.joshua.a51bike.R;

import java.lang.reflect.Field;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-02-10
 */
public class BaseSlideCloseActivity extends BaseActivity implements
        SlidingPaneLayout.PanelSlideListener {
    private static final String TAG = "BaseSlideCloseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSlideBackClose();
    }

    private void initSlideBackClose() {
        if (isSupportSwipeBack()) {
            SlidingPaneLayout slidingPaneLayout = new SlidingPaneLayout(this);
            // 通过反射改变mOverhangSize的值为0，
            // 这个mOverhangSize值为菜单到右边屏幕的最短距离，
            // 默认是32dp，现在给它改成0
            try {
                Field overhangSize = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
                overhangSize.setAccessible(true);
                overhangSize.set(slidingPaneLayout, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            slidingPaneLayout.setPanelSlideListener(this);
            slidingPaneLayout.setSliderFadeColor(getResources()
                    .getColor(android.R.color.transparent));

            // 左侧的透明视图
            View leftView = new View(this);
            leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            slidingPaneLayout.addView(leftView, 0);

            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();


            // 右侧的内容视图
            ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
            decorChild.setBackgroundColor(getResources()
                    .getColor(android.R.color.white));
            decorView.removeView(decorChild);
            decorView.addView(slidingPaneLayout);

            // 为 SlidingPaneLayout 添加内容视图
            slidingPaneLayout.addView(decorChild, 1);
        }
    }
    protected boolean isSupportSwipeBack() {
        return true;
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

    /**
     * Called when a sliding pane's position changes.
     *
     * @param panel       The child view that was moved
     * @param slideOffset The new offset of this sliding pane within its range, from 0-1
     */
    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    /**
     * Called when a sliding pane becomes slid completely open. The pane may or may not
     * be interactive at this point depending on how much of the pane is visible.
     *
     * @param panel The child view that was slid to an open position, revealing other panes
     */
    @Override
    public void onPanelOpened(View panel) {

    }

    /**
     * Called when a sliding pane becomes slid completely closed. The pane is now guaranteed
     * to be interactive. It may now obscure other views in the layout.
     *
     * @param panel The child view that was slid to a closed position
     */
    @Override
    public void onPanelClosed(View panel) {

    }
}