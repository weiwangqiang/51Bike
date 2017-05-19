package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.customview.CircleImageView;
import com.joshua.a51bike.customview.progress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.imageUtil.ImageManager;

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
 * @since 2017-05-19
 */
@ContentView(R.layout.left_main)
public class LeftMain extends BaseActivity {
    private static final String TAG = "LeftMain";
    private TextView textView1,textView2,textView3,textView4;
    private List<TextView> list = new ArrayList<>();
    private TextView userName,userMoney,userCash;
    private CircleImageView userIcn;
    private View useProgressParent;
    private progress myProgress;

    @ViewInject(R.id.main_progress_view)
    private progress useProgress;

    @ViewInject(R.id.scan_title)
    private TextView scan_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        scan_title.setText("个人中心");
    }

    public void init() {
        findId();
        setLister();
    }

    public void findId() {
        useProgressParent = findViewById(R.id.main_progress);
        userIcn = (CircleImageView) findViewById(R.id.main_user_icn);
        userName = (TextView) findViewById(R.id.main_user_name);
        userMoney = (TextView) findViewById(R.id.money);//余额
        userCash = (TextView) findViewById(R.id.cash);//保证金
        myProgress = (progress) findViewById(R.id.main_progress_view);

        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView3 = (TextView) findViewById(R.id.text3);
        textView4 = (TextView) findViewById(R.id.text4);

        list.add(textView1);
        list.add(textView2);
        list.add(textView3);
        list.add(textView4);
    }

    public void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);
        //LeftMain
        userIcn.setOnClickListener(this);
        findViewById(R.id.main_setting).setOnClickListener(this);
        findViewById(R.id.user_route).setOnClickListener(this);
        findViewById(R.id.account).setOnClickListener(this);
        findViewById(R.id.service).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        initUI();
    }

    /**
     * 判断是否显示侧滑栏的进度条
     * @param user
     */
    private void showProgreesOrNot(User user) {
        setProgressView(1);   //更新
        if(user == null ){
            useProgressParent.setVisibility(View.GONE);
            return;
        }
        if(useProgressParent.getVisibility() == View.GONE)
            useProgressParent.setVisibility(View.VISIBLE);
        if(user.getRealName() != null )
            setProgressView(2);
        if(user.getUserRerve() == 200)
            useProgressParent.setVisibility(View.GONE);
    }

    /**
     * 设置当前进度条
     * @param a
     */
    private void setProgressView(int a) {
        useProgress.setPoistion(a);
        int i ;
        for(TextView T:list){
            T.setTextColor(getResources().getColor(R.color.gray));
        }
        for(i = 0;i<a;i++){
            list.get(i).setTextColor(getResources().getColor(R.color.baseColor));
        }
    }

    /*判断用户是否处于陆状态*/
    private void initUI() {
        if(null != userControl.getUser())
            initUserMessage();
        else
            initLogOut();
    }

    /**
     * 初始化用户信息
     */
    String pre_image_path = Environment.getExternalStorageDirectory()+"/51get";

    /**
     * 未登录状态
     */
    private void initLogOut() {
        userName.setText("未登录");
        userMoney.setText("0");
        userCash.setText("0");
        setProgressView(0);
        if(useProgressParent.getVisibility() == View.GONE)
            useProgressParent.setVisibility(View.VISIBLE);
        userIcn.setImageResource(R.drawable.default_icn);
    }

    /**
     * 初始化用户信息
     */
    private void initUserMessage() {
        User user = userControl.getUser();
        String after_image_path = "";
        userName.setText(userControl.getUser().getUsername());
        userMoney.setText(userControl.getUser().getUsermoney()+"");
        userCash.setText(userControl.getUser().getUserRerve()+"");
        showProgreesOrNot(user);
        if(after_image_path == null )
            return;
        after_image_path = pre_image_path +"/"+userControl.getUser().getUsername()+".jpg";
        ImageManager manager = new  ImageManager();
        if(userControl.getUser().getUserpic() != null){
            manager.bindImageWithBitmap(userIcn,
                    after_image_path);
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
            case R.id.main_user_icn:
                userControl.toChoice(LeftMain.this);
                break;
            case R.id.main_setting:
                userControl.config(LeftMain.this);
                break;
            case R.id.user_route:
                userControl.userRoute(LeftMain.this);
                break;
            case R.id.service:
                userControl.service(LeftMain.this);
                break;
            case R.id.account:
                userControl.account(LeftMain.this);
                break;
            case R.id.share:
                userControl.share(LeftMain.this);
                break;
            case R.id.main_use_explain:
                startActivity(new Intent(this, Use_Explain.class));
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