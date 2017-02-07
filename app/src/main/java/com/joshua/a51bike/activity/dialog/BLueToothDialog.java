package com.joshua.a51bike.activity.dialog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.joshua.a51bike.Interface.BlueToothLister;
import com.joshua.a51bike.Interface.MyAlerDialog;
import com.joshua.a51bike.R;
import com.joshua.a51bike.receiver.BlueToothReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-02-03
 */
public class BLueToothDialog extends MyAlerDialog
        implements BlueToothLister, View.OnClickListener,ListView.OnItemClickListener{
    private String TAG = "BLueToothDialog";
    private Button search;
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private BlueToothReceiver receiver;
    private Context context;
    private int REQUEST_ENABLE_BT = 200;

    private String Name = "name";
    private String Address = "address";
    private SimpleAdapter adapter;
    private List<HashMap<String,String>> data = new ArrayList<>();
    public BLueToothDialog(Context context) {
        super(context);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bluetooth);
        getWindow().setBackgroundDrawable(new ColorDrawable());
        init();
    }

    private void init() {
        findid();
        setListener();
        initBlueTooth();
        initBlueToothListView();
    }

    private void initBlueToothListView() {
        adapter = new SimpleAdapter(context,data,
                R.layout.bluetooth_item,
                new String[]{Name},
                new int[]{R.id.text});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void setListener() {
        search.setOnClickListener(this);
    }

    private void initBlueTooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        receiver = new BlueToothReceiver(this);
        //检查是否支持蓝牙
        if(null == adapter){
            // Device does not support Bluetooth
            Toast.makeText(context,"手机不支持蓝牙！",0).show();
            cancel();
            return ;
        }

    }

    private void findid() {
        listView = (ListView) findViewById(R.id.dialog_listView);
        search = (Button) findViewById(R.id.search);
    }

    @Override
    public void myShow() {
        show();
    }

    @Override
    public void myCancel() {
        cancel();
    }

    @Override
    public void getDate(BluetoothDevice bluetoothDevice) {
        HashMap<String,String> map = new HashMap<>();
        map.put(Name,bluetoothDevice.getName());
        map.put(Address,bluetoothDevice.getAddress());
        data.add(map);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            if (bluetoothAdapter != null) {
                bluetoothAdapter.cancelDiscovery();
            }
            context.unregisterReceiver(receiver);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:
                searchDevice();
                break;
            default:
                break;
        }
    }

    private void searchDevice() {
        //检查蓝牙是否可用
        if (!bluetoothAdapter.isEnabled()) {
            //不可用，就发送intent 请求
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.putExtra(BluetoothAdapter.EXTRA_LOCAL_NAME,"51get");
            ((Activity)context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return ;
        }
        getBlueToothMes();

    }
    /**
     * 获取蓝牙信息
     */
    private void getBlueToothMes() {
        //指示是否已成功启动发现操作
        Boolean b = bluetoothAdapter.startDiscovery();
        Log.i(TAG, "getBlueToothMes: begin to start ? "+b);
        if(b){
            bindBroadCast();
        }
        Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
//        adapter.listenUsingRfcommWithServiceRecord(String+"", bluetoothDevices.);
    }

    /**
     * 绑定蓝牙广播
     */
    private void bindBroadCast() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filter); // Don't forget to unregister during onDestroy
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String adress = data.get(position).get(Address);

    }
}
