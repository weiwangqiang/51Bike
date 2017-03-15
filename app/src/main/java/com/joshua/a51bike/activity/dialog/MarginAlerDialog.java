package com.joshua.a51bike.activity.dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.joshua.a51bike.Interface.MyAlerDialog;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;

/**保证金
 * Created by wangqiang on 2017/1/9.
 */

public class MarginAlerDialog extends MyAlerDialog implements View.OnClickListener{
    String TAG ="MarginAlerDialog";
    private TextView t;
    private TextView c;
    private TextView cancel;
    private TextView sure;
    private String title;
    private String content;

    private Activity context;
    public MarginAlerDialog(Activity context,String title,String content) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_white);
        getWindow().setBackgroundDrawable(new ColorDrawable());

        t = (TextView)findViewById(R.id.dialog_title);
         c = (TextView)findViewById(R.id.dialog_content);
         cancel = (TextView) findViewById(R.id.dialog_cancel);
         sure = (TextView) findViewById(R.id.dialog_sure);

        t.setText(title);
        c.setText(content);
        cancel.setOnClickListener(this);
        sure.setOnClickListener(this);
    }
    @Override
    protected  void onStart(){
        super.onStart();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_cancel:
                cancel();
                break;
            case R.id.dialog_sure:
                UserControl u = UserControl.getUserControl();
                u.saoma(context);
                cancel();
                break;
        }
    }

    @Override
    public void myShow() {
        show();
    }

    @Override
    public void myCancel() {
        cancel();
    }
}
