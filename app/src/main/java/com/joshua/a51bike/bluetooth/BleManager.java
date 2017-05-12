package com.joshua.a51bike.bluetooth;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.joshua.a51bike.Interface.BleCallBack;
import com.joshua.a51bike.activity.control.CarControl;
import com.joshua.a51bike.bluetooth.utils.Protocol;
import com.joshua.a51bike.util.UiUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;


/**
 * ============================================================
 * <p>
 * 版 权 ： 吴奇俊  (c) 2017
 * <p>
 * 作 者 : 吴奇俊
 * <p>
 * 版 本 ： 1.0
 * <p>
 * 创建日期 ： 2017/4/20 20:38
 * <p>
 * 描 述 ：完成与BLE设备通信的管理
 * <p>
 * ============================================================
 **/

public class BleManager {
    private final static String TAG = "BleManager";

    private final Activity context;//用于获取系统BLE服务
    private BluetoothManager mBluetoothManager;//Ble蓝牙管理器，用于管理手机固有的Ble硬件
    private BluetoothAdapter mBluetoothAdapter;//Ble蓝牙适配器，用于判断手机当前Ble设备是否开启
    private String mBluetoothDeviceAddress;//保存的上一次链接的设备地址
    private BluetoothGatt mBluetoothGatt;
    private String mDeviceId;//设备ID
    private static final int REQUEST_ENABLE_BT = 0x01;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;


    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    //设备主动上报信息的地址
    private final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private static final String serviceUuid = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String writeUuid = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String readUuid = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    private static final byte startCommand = 0x01;//开机命令
    private BleCallBack mBleCallBack;


    /**
     * 在构造器中
     * 完成Ble的初始化工作
     */
    public BleManager(Activity context,BleCallBack bleCallBack) {
        this.context = context;
        this.mBleCallBack=bleCallBack;
    }

    public BluetoothGatt getmBluetoothGatt(){
        return mBluetoothGatt;
    }

    /**
     * 检查设备是否支持BLE蓝牙，并初始化mBluetoothManager与mBluetoothAdapter
     * 返回初始化结果
     * true or false
     */
    public boolean checkAndInit() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持返回false
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    /**
     * 检查设备是否打开了蓝牙
     * 如果当前蓝牙设备没启用
     * 弹出对话框向用户要求授予权限来启用
     * 调用该方法前要先调用checkAndInit
     */
    public void checkOpenBLE() {
        if(mBluetoothAdapter == null) return;
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        Log.i(TAG, "onActivityResult: ----------->> 开启蓝牙配对模式啦===========");
        Intent in=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600); //3600为蓝牙设备可见时间
        context.startActivity(in);
    }


    /**
     * 尝试连接ble设备
     * 返回尝试连接结果
     * 此时若返回true，并不能确定已经连接成功，还需要看mGattCallback回调结果
     */
    public boolean connect() {
        mDeviceId = CarControl.getCarControl().getCar().getCarMac();//扫码获取的
        Log.i(TAG, "connect: mDevice  "+mDeviceId);
        String address = getDeviceAddress(mDeviceId);
        Log.i(TAG, "connect: address  "+address);
        //判断mBluetoothAdapter与address是否存在
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "connect: ==============");
            return false;
        }
        Log.i(TAG, "connect: ---------------------- ");
        //先前连接的设备。 尝试重新连接
        if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.i(TAG, "connect: connect  two  ");
            if (mBluetoothGatt.connect()) {
                Log.i(TAG, "connect: connnect scucess ");
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        //获取ble设备
        final BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        Log.i(TAG, "GET BLe device"+device.getAddress());
        refreshDeviceCache();
        //真正进行连接的方法
        //android 6 设置为false
        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
//        mBluetoothGatt.registerApp(mBluetoothGatt);
        Log.i(TAG, "Trying to create a new connection.");
        //保存当前设备的地址，便于下次连接
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }
    /**
     * Clears the internal cache and forces a refresh of the services from the
     * remote device.
     */
    public boolean refreshDeviceCache() {
        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod(
                        "refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(
                            localBluetoothGatt, new Object[0])).booleanValue();
                    //如果
//                    mBluetoothGatt.disconnect();
//                    mBluetoothGatt.close();
                    return bool;
                }
            } catch (Exception localException) {
                Log.i(TAG, "An exception occured while refreshing device");
            }
        }
        return false;
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
     * 向设备发送开锁命令
     * 回调
     */
    public void startBike() {
        sendCommandToDevice(false);
    }

    public static final int BIKE_START = 0x04;
    public static final int BIKE_STOP = 0x08;
    public static final int BIKE_ERROR = 0x16;


    private int parseBytes(byte[] resultBytes) {
        //功能码为5
        byte model = resultBytes[0];
        String str_model = model + "";
        if (!str_model.equals("5")) {
            return BIKE_ERROR;
        }
        //开关机
        byte state = resultBytes[3];
        String str_state = state + "";
        if (str_state.equals("0")) {
            return BIKE_STOP;
        } else if (str_state.equals("1")){
            return BIKE_START;
        }
        return BIKE_ERROR;
    }
    /**
     * 根据uuid获取Characteristic 来进行读写操作
     * 读 or 写
     */
    private BluetoothGattCharacteristic getCharacteristic(String uuid) {
        List<BluetoothGattService> services = this
                .getSupportedGattServices();
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
    public void sendCommandToDevice(boolean isContinue) {
        Log.i(TAG, "sendCommandToDevice:--------------向设备发送启动数据了");
        byte[] bytes = Protocol.getBytes(mDeviceId, startCommand);
        BluetoothGattCharacteristic writeCharacteristic= getCharacteristic(writeUuid);

        if(!isContinue){
            byte[] pre_20 = new byte[20];
            System.arraycopy(bytes, 0, pre_20, 0, 20);
            writeCharacteristic.setValue(pre_20);
            writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            this.writeCharacteristic(writeCharacteristic);
        } else {
            byte[] after_5 = new byte[5];
            System.arraycopy(bytes, 20, after_5, 0, 5);
            writeCharacteristic.setValue(after_5);
            writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            this.writeCharacteristic(writeCharacteristic);
        }
    }
    /**
     * 向Ble设备读取数据
     */
    public void readStateFromDevice(){
        Log.i(TAG, "readStateFromDevice: ================   readUuid \n "+readUuid);
        BluetoothGattCharacteristic readCharacteristic = getCharacteristic(readUuid);
        this.setCharacteristicNotification(
                readCharacteristic, true);
        this.readCharacteristic(readCharacteristic);
    }


    /**
     * 向ble设备写数据
     */
    private void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);

    }
    /**
     * 从ble设备读数据
     */
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.i(TAG, "readCharacteristic: 开始读数据啦 ");
        mBluetoothGatt.readCharacteristic(characteristic);
    }
    /**
     * 设置可观察
     */
    private void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
                .fromString(CLIENT_CHARACTERISTIC_CONFIG));

//        BluetoothGattDescriptor descriptor1 = characteristic.
//                getDescriptor(com.characteristic.smart.smartpen.bluetooth.HD_Profile.UUID_CHAR_NOTIFY_DIS);
//        if(descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)){ }
        if (descriptor != null) {
            Log.w(TAG, "desc not null , set notify");
            descriptor .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);//  android 6 设置这个
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * 获取ble设备的service列表
     */
    private List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null){
            Log.d(TAG, "getSupportedGattServices: mBluetoothGatt==null");
            return null;
        }
        Log.d(TAG, "getSupportedGattServices: services size:"+mBluetoothGatt.getServices().size());
        return mBluetoothGatt.getServices();
    }
    /**
     * 断开连接
     * 回收资源
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        }else{
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            Log.i(TAG, "disconnect: mBluetoothGatt 断开了 ");
        }

    }

    private boolean isNotifi = false;//判定是否需要提示连接成功
    //Gatt协议读写通信回调
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        //gatt连接结果回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG, "onConnectionStateChange: "+status +" : "+newState);
            Log.i(TAG, "onConnectionStateChange: =============================");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.w(TAG, "onConnectionStateChange STATE_CONNECTED ");
                //保存当前状态
                mConnectionState = STATE_CONNECTED;
                Log.w(TAG, "onConnectionStateChange try to find services ");
                //发现服务
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.w(TAG, "onConnectionStateChange STATE_DISCONNECTED ");
                mConnectionState = STATE_DISCONNECTED;
                //连接失败
                mBleCallBack.onGattConnect(ACTION_GATT_DISCONNECTED);
            }
        }

        //发现服务后的回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered:  -==========  ");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //真正通知前台设备链接成功
                mBleCallBack.onGattConnect(ACTION_GATT_CONNECTED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead: ================ ");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (int i = 0; i < characteristic.getValue().length; i++) {
                    System.out.println(i + "--------read success----- characteristic:" + characteristic.getValue()[i]);
                }
            }
        }
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            Log.i(TAG, "onDescriptorWrite: ===========================  ");
            System.out.println("onDescriptorWrite = " + status
                    + ", descriptor =" + descriptor.getUuid().toString());
        }

        /**
         * Callback indicating the MTU for a given device connection has changed.
         * <p>
         * This callback is triggered in response to the
         * {@link BluetoothGatt#requestMtu} function, or in response to a connection
         * event.
         *
         * @param gatt   GATT client invoked {@link BluetoothGatt#requestMtu}
         * @param mtu    The new MTU size
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the MTU has been changed successfully
         */
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.i(TAG, "onMtuChanged: ===============");
        }


        //向BLE设备写入成功后的回调
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicWrite: ======================");
            for (int i = 0; i < characteristic.getValue().length; i++) {
                int result = characteristic.getValue()[i] & 0xff;
                System.out.println(i + "--------write success----- characteristic:" + result);
            }
            //由于每次只能发送20字节的数组，因此要判断是否需要继续发送
            if(characteristic.getValue().length==20){
                mBleCallBack.onCharacteristicContinueWrite();
            }else if(characteristic.getValue().length==5){
                mBleCallBack.onCharacteristicFinishWrite();
            }
        }

        //获取BLE设备返回信息的回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged: ===============================");
            byte[] resultBytes=new byte[10];

            for (int i = 0; i < (characteristic.getValue().length-1); i++) {
                resultBytes[i]=characteristic.getValue()[i];
                System.out.println(i + "--------change success----- characteristic:" + resultBytes[i]);
            }
            if(resultBytes[3] == 1){
                if(!isNotifi){
                    UiUtils.showToast("启动成功");
                    isNotifi = true;
                }
            }
            mBleCallBack.getStateFromDevice(parseBytes(resultBytes));
        }
    };
}