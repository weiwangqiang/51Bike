package com.joshua.a51bike.activity.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.joshua.a51bike.Interface.BlueToothLister;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.receiver.BlueToothReceiver;

import org.xutils.view.annotation.ContentView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-26
 */
@ContentView(R.layout.bluetooth)
public class BlueTooth extends BaseActivity
        implements  View.OnClickListener,ListView.OnItemClickListener, BlueToothLister {
    private String TAG = "BlueTooth" ;
    private Set<BluetoothDevice> bluetoothDevices ;
    private TextView textView;
    private BluetoothDevice bluetoothDevice;
    private List<BluetoothDevice> list = new ArrayList<>();


    private Button search;
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private BlueToothReceiver receiver;
    private int REQUEST_ENABLE_BT = 200;

    private String Name = "name";
    private String Address = "address";
    private String My_Address;
    private SimpleAdapter adapter;
    private List<HashMap<String,String>> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    @Override
    protected void onResume(){
        super.onResume();
        initBlueTooth();

    }
    private void init() {
        findid();
        setListener();
        initBlueToothListView();

    }

    private void findid() {
        listView = (ListView) findViewById(R.id.dialog_listView);
        search = (Button) findViewById(R.id.search);
    }

    private void setListener() {
        search.setOnClickListener(this);
    }

    private void initBlueToothListView() {
        adapter = new SimpleAdapter(this,data,
                R.layout.bluetooth_item,
                new String[]{Name,Address},
                new int[]{R.id.text,R.id.address});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }
    private void initBlueTooth() {
        bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter ();
        Log.i(TAG, "initBlueTooth: local address is " + bluetoothAdapter.getAddress() );
        My_Address = bluetoothAdapter.getAddress();
//        adapter.setName("51get");
        receiver = new BlueToothReceiver(this);
        //检查是否支持蓝牙
        if(null == bluetoothAdapter){
            // Device does not support Bluetooth
            Toast.makeText(this,"手机不支持蓝牙！",Toast.LENGTH_SHORT).show();
            return ;
        }
        //检查蓝牙是否可用
        if (!bluetoothAdapter.isEnabled()) {
            //不可用，就发送intent 请求
            search.setText("蓝牙还没打开，现在打开？");
            return ;
        }
        startDiscovery();
    }

    /**
     * 打开蓝牙搜索
     */
    private void startDiscovery() {
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
     * 绑定蓝牙时间广播
     */
    private void bindBroadCast() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter); // Don't forget to unregister during onDestroy
    }
    public void setLister() {
        findViewById(R.id.left_back).setOnClickListener(this);
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
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else
        {
            startDiscovery();
        }

    }

    private void Connect() {
        String text = textView.getText().toString();
        bluetoothAdapter.getRemoteDevice(bluetoothDevice.getAddress());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if (bluetoothAdapter != null) {
                bluetoothAdapter.cancelDiscovery();
            }
                unregisterReceiver(receiver);
            }catch(Exception e){
                    e.printStackTrace();
                }
    }

    @Override
    protected void onActivityResult (int requestCode,
                           int resultCode,
                           Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_ENABLE_BT ){
            switch (resultCode){
                case  RESULT_OK:
                    Log.i(TAG, "onActivityResult: ok ");
                    search.setText("正在给母猪配对。。。");
                    startDiscovery();
                    break;
                case RESULT_CANCELED :
                    Log.i(TAG, "onActivityResult: fail");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void getDate(BluetoothDevice bluetoothDevice) {
        HashMap<String,String> map = new HashMap<>();

        map.put(Name,bluetoothDevice.getName());
        map.put(Address,bluetoothDevice.getAddress());
        data.add(map);
        adapter.notifyDataSetChanged();
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       final  String address = data.get(position).get(Address);
       final  BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        bluetoothAdapter.cancelDiscovery();
        new ConnectThread(device).start();
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(My_Address));
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
//            manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}