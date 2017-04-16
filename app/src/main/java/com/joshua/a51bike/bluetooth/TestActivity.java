package com.joshua.a51bike.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.joshua.a51bike.R;
import com.joshua.a51bike.bluetooth.utils.Protocol;

import java.util.List;
import java.util.UUID;

public class TestActivity extends AppCompatActivity {
    private String TAG = "TestActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mBluetoothLeService;
    private static final int REQUEST_ENABLE_BT = 0x01;
    private String mDeviceAddress;
    private String mDeviceId;
    private static final String serviceUuid = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String writeUuid = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String readUuid = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    private static final byte startCommand = 0x01;
    private static final byte stopCommand = 0x02;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        checkPhoneState();
    }

    /**
     * 连接ble设备
     */
    public void connect_ble(View view) {
        Log.i(TAG, "connect_ble: ");
        mDeviceId = "EA8F2B98C3E8FFFFFFFFFFFF";//扫码获取的
        mDeviceAddress = getDeviceAddress(mDeviceId);//
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection,
                BIND_AUTO_CREATE);
    }

    /**
     * 检查设备是否支持蓝牙
     */
    private void checkPhoneState() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "设备不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 将扫描到的设备id转换成设备MAC地址
     * 格式为 E8:99:B6:C8:A9:B9
     */
    private String getDeviceAddress(String deviceId) {
        String preAddress = deviceId.substring(0, 12);
        StringBuilder deviceAddress = new StringBuilder();
        for (int i = 0; i < preAddress.length(); i++) {
            deviceAddress.append(preAddress.substring(i, i++ + 2));
            if (i != preAddress.length() - 1) {
                deviceAddress.append(":");
            }
        }
        return deviceAddress.toString();
    }

    /**
     * 连接Service的接口
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            Log.i(TAG, "onServiceConnected: ");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if(mBluetoothLeService == null)
                 Log.i(TAG, "onServiceConnected: mBluetoothLeService is null  ");
            if (!mBluetoothLeService.initialize()) {
                Toast.makeText(TestActivity.this, "蓝牙初始化失败", Toast.LENGTH_SHORT).show();
                finish();
            }
            //链接Service成功，则通过该Service尝试连接蓝牙设备
            if (mBluetoothLeService.connect(mDeviceAddress))
                Toast.makeText(TestActivity.this, "设备连接成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService=null;

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } else {
            Toast.makeText(this, "成功打开蓝牙", Toast.LENGTH_SHORT).show();
}
        super.onActivityResult(requestCode, resultCode, data);
        }

    /**
     * 根据uuid获取Characteristic
     */
    private BluetoothGattCharacteristic getCharacteristic(String uuid) {
        Log.i(TAG, "getCharacteristic: ");
        List<BluetoothGattService> services = mBluetoothLeService
                .getSupportedGattServices();
        Log.i(TAG, "getCharacteristic: size is "+services.size());
        for (BluetoothGattService service : services) {
            if (service.getUuid().toString().equals(serviceUuid)) {
                return service.getCharacteristic(UUID.fromString(uuid));
            }
        }
        return null;
    }

    /**
     * 执行具体的通信任务
     * 数据分两次发送
     * 发送完毕后读取设备返回信息
     */
    private void executeBle(byte[] bytes) {
        byte[] pre_20 = new byte[20];
        System.arraycopy(bytes, 0, pre_20, 0, 20);
        byte[] after_5 = new byte[5];
        System.arraycopy(bytes, 20, after_5, 0, 5);
        BluetoothGattCharacteristic writeCharacteristic = getCharacteristic(writeUuid);
        writeCharacteristic.setValue(pre_20);
        writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBluetoothLeService.writeCharacteristic(writeCharacteristic);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writeCharacteristic.setValue(after_5);
        writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBluetoothLeService.writeCharacteristic(writeCharacteristic);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BluetoothGattCharacteristic readCharacteristic = getCharacteristic(readUuid);
        mBluetoothLeService.setCharacteristicNotification(
                readCharacteristic, true);
        mBluetoothLeService.readCharacteristic(readCharacteristic);
    }

    /**
     * 开锁
     */
    public void startBike(View view) {
        byte[] bytes = Protocol.getBytes(mDeviceId, startCommand);
        executeBle(bytes);
    }


    /**
     * 还车
     */
    public void returnBike(View view) {
        byte[] bytes = Protocol.getBytes(mDeviceId, stopCommand);
        executeBle(bytes);
    }

    /**
     * 锁车
     */
    public void lockBike(View view) {

    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] resultBytes = intent.getByteArrayExtra("resultBytes");
                parseBytes(resultBytes);

            }
        }
    };

    private void parseBytes(byte[] resultBytes) {
      //功能码为5
        byte model=resultBytes[0];
        String str_model=model+"";
       //// TODO: 2017/3/29

        //开关机
        byte state=resultBytes[3];
        String str_state=state+"";
        if(str_state.equals("0")){
            Toast.makeText(this, "成功还车", Toast.LENGTH_SHORT).show();
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
}
