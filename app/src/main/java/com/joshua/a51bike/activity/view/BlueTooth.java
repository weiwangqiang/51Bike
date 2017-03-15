package com.joshua.a51bike.activity.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.joshua.a51bike.R.layout.bluetooth;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-26
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@ContentView(bluetooth)
public class BlueTooth extends BaseActivity
        implements  View.OnClickListener,ListView.OnItemClickListener, BlueToothLister {
    private String TAG = "BlueTooth" ;
    private Set<BluetoothDevice> bluetoothDevices ;
    private TextView textView;
    private BluetoothDevice bluetoothDevice;


    private Button search;
    private ListView listView;
    private BluetoothAdapter mBluetoothAdapter;
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
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,"not support !", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= 18) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        if(mBluetoothAdapter == null ) return;
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
//        scanLeDevice(mBluetoothAdapter.isEnabled());
    }

    /**
     * 打开蓝牙搜索
     */
    private Handler mHandler = new Handler();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private void scanLeDevice(final boolean enable) {
        Log.i(TAG, "scanLeDevice: is "+enable);
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= 18) {
//                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT >= 18) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 18) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

            }
        }
    }
    private List<BluetoothDevice> list = new ArrayList<>();
    private BluetoothGatt mBluetoothGatt;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
//    public final static UUID UUID_HEART_RATE_MEASUREMENT =
//            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    private boolean isConnect = false;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: result is "+device.toString());
                            if(!isConnect){
                                Log.i(TAG, "run: connect");
                                mBluetoothGatt =
                                        device.connectGatt(BlueTooth.this, false, bluetoothGattCallback);
                            }
                            list.add(device);
                            if (Build.VERSION.SDK_INT >= 18) {
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);

                            }
                        }
                    });
                }
            };
    /*蓝牙连接回调
    * */
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback(){
        @Override
       public void onServicesDiscovered (BluetoothGatt gatt,
                                   int status){
            Log.i(TAG, "onServicesDiscovered: status is "+status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
        }
        @Override
       public  void onConnectionStateChange (BluetoothGatt gatt,
                                      int status,
                                      int newState){
            Log.i(TAG, "onConnectionStateChange: newState "+newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                intentAction = ACTION_GATT_CONNECTED;
//                mConnectionState = STATE_CONNECTED;
//                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                intentAction = ACTION_GATT_DISCONNECTED;
//                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
//                broadcastUpdate(intentAction);
            }
        }
        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }


        @Override
// Characteristic notification
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
//            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };



    public void close() {
        Log.i(TAG, "close: ");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    /**
     *
     * 绑定蓝牙时间广播
     */
    private void bindBroadCast() {
        Log.i(TAG, "bindBroadCast: ");
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
                scanLeDevice(mBluetoothAdapter.isEnabled());

                break;
            default:
                break;
        }
    }

    private void searchDevice() {
        //检查蓝牙是否可用
        if (!mBluetoothAdapter.isEnabled()) {
            //不可用，就发送intent 请求
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.putExtra(BluetoothAdapter.EXTRA_LOCAL_NAME,"51get");
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else
        {
//            startDiscovery();
        }

    }

    private void Connect() {
        String text = textView.getText().toString();
        mBluetoothAdapter.getRemoteDevice(bluetoothDevice.getAddress());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{

            if (Build.VERSION.SDK_INT >= 18) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

            }

            if (mBluetoothAdapter != null) {
                if (Build.VERSION.SDK_INT >= 18) {
                    close();

                }

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
                    scanLeDevice(mBluetoothAdapter.isEnabled());

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
        Log.i(TAG, "getDate: ");
        Set<BluetoothDevice> bluetoothDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice b : bluetoothDevices){

            HashMap<String,String> map = new HashMap<>();
            map.put(Name,b.getName());
            map.put(Address,b.getAddress());
            data.add(map);

        }
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
       final  BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothAdapter.cancelDiscovery();
//        new ConnectThread(device).start();
    }
}