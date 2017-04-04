package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.entity.school.School;
import com.joshua.a51bike.entity.school.SchoolList;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-28
 */
@ContentView(R.layout.choice_province)
public class ChoiceSchool extends BaseActivity {
    private static final String TAG = "ChoiceProvince";
    private final int GET_PRO_RESULT = 110;

    @ViewInject(R.id.choice_listView)
    private ListView listView;

    @ViewInject(R.id.choice_title)
    private TextView title;

    private SimpleAdapter adapter;
    private String url = "http://www.hisihi.com/app.php?s=/school/school/provinceid/";
    private List<Map<String,String>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String pro_id = intent.getStringExtra("id");
        url = url + pro_id;
        Log.i(TAG, "onCreate: yrl "+url );
        title.setText("请选择学校");
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void init() {
        initActionBar();
        findId();
        setLister();
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();

        RequestParams params = new RequestParams(url);
        post(params);
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

    }

    public void setLister() {

    }

    private   Callback.Cancelable cancelable;

    private void post(RequestParams params){
        Log.i(TAG, "post:");
        cancelable =  x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dialogControl.cancel();
                addDataToList(result);
                initList();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                uiUtils.showToast("失败！");
                dialogControl.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                dialogControl.cancel();

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void addDataToList(String result) {
        School p = new Gson().fromJson(result, School.class);
        for(SchoolList pro : p.getData()){
            Map<String,String> map = new HashMap<>();
            map.put("name",pro.getSchool_name());
            map.put("id",pro.getSchool_name());
            list.add(map);
            Log.i(TAG, "onSuccess: pro name "+pro.getSchool_name());
        }
    }

    public void initList(){
        adapter = new SimpleAdapter(this,
                list,R.layout.choice_province_item,
                new String[]{"name"},new int[]{R.id.choice_item_text});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map = list.get(position);
                String sch_name = map.get("name");
                Intent intent = new Intent();
                intent.putExtra("school",sch_name);
                setResult(GET_PRO_RESULT,intent);
                finish();
                Log.i(TAG, "onItemClick: name"+sch_name);
            }
        });
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
  
  
  
  
