package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-29
 */
@ContentView(R.layout.web_view)
public class WebView extends BaseActivity {
    private static final String TAG = "WebView";
    private String url = "";

    @ViewInject(R.id.browse_title)
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Intent intent = getIntent();
        String t = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        title.setText(t);
    }

    public void init() {
        initActionBar();
        findId();
        setLister();
    }

    private void initActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.title_back));
        setSupportActionBar(myToolbar);

    }

    public void findId() {

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
  
  
  
  
