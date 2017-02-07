package com.joshua.a51bike.activity.view;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.DialogControl;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.activity.dialog.WaitProgress;
import com.joshua.a51bike.entity.User;
import com.joshua.a51bike.util.AppUtil;
import com.joshua.a51bike.util.JsonUtil;
import com.joshua.a51bike.util.MyTools;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */
@ContentView(R.layout.login)
public class Login extends BaseActivity {
    private String TAG = "Login";
    private UserControl userControl;
    private String url = AppUtil.BaseUrl + "/user/login";
    private EditText getName, getPass;
    private Button login;
    private RequestQueue requestQueue;
    private DialogControl dialogControl;
    private final int SEND_DIALOG = 0x1;

    private Handler handler = new Handler() {
        public void handleMessage(Message mes){
            switch (mes.what){
                case SEND_DIALOG:
                    dialogControl.cancel();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        userControl = UserControl.getUserControl();
        requestQueue = Volley.newRequestQueue(Login.this);
        dialogControl = DialogControl.getDialogControl();
        findId();
        setLister();
    }

    public void findId() {
        getName = (EditText) findViewById(R.id.login_Admin);
        getPass = (EditText) findViewById(R.id.login_Pass);
        login = (Button) findViewById(R.id.login_button);
        getName.setText("wei");
        getPass.setText("123456");
    }

    public void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);
//        findViewById(R.id.Login_register).setOnClickListener(this);
        login.setOnClickListener(this);
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
            case R.id.login_button:
                Login();
                break;
            default:
                break;
        }
    }

    private void Login(){
        if (MyTools.EditTextIsNull(getName) || MyTools.EditTextIsNull(getPass)) {
            return;
        }
        String name = getName.getText().toString();
        String pass = getPass.getText().toString();
        User user = new User();
        user.setUserpass(pass);
        user.setUsername(name);
        JSONObject jsonObject = JsonUtil.getJSONObject(user);
        dialogControl.setDialog(new WaitProgress(this));
        dialogControl.show();
        if(null != jsonObject){
            postjson(jsonObject);
        }
    }

    public void post(){
        RequestParams params = new RequestParams(url);

        Callback.Cancelable cancelable
                = x.http().get(params,  new Callback.CommonCallback<List<RequestParams>>() {
            @Override
            public void onSuccess(List<RequestParams> result) {
                Toast.makeText(x.app(), result.get(0).toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void postjson(JSONObject object) {
        JsonRequest<JSONObject> jsonResquest =
                new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        Log.e("login", "login response is " + object.toString());
                        handler.sendEmptyMessage(SEND_DIALOG);
                    }
                },
                        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("error", "login Error is " + volleyError);
                handler.sendEmptyMessage(SEND_DIALOG);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("dat    aType", "json");
                headers.put("ContentType", "application/json; charset=UTF-8");
                return headers;
            }
        };
        requestQueue.add(jsonResquest);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}