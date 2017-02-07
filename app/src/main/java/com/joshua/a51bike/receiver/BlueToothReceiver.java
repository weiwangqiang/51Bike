package com.joshua.a51bike.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.joshua.a51bike.Interface.BlueToothLister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-27
 */
public class BlueToothReceiver extends BroadcastReceiver{
    private BluetoothAdapter adapter;
    private String TAG = "BlueToothReceiver";
    private Map<String,String> map = new HashMap<>();
    private List<BluetoothDevice> list;
    private BlueToothLister lister;
    public BlueToothReceiver(BlueToothLister lister){
        this.lister = lister;
    }
    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context#registerReceiver(BroadcastReceiver,
     * IntentFilter, String, Handler)}. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * {@link Context#startService(Intent)} instead of
     * {@link Context#bindService(Intent, ServiceConnection, int)}.  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     *注意：执行设备发现对于蓝牙适配器而言是一个非常繁重的操作过程，并且会消耗大量资源。
     *  在找到要连接的设备后，确保始终使用 cancelDiscovery() 停止发现，然后再尝试连接。
     *  此外，如果您已经保持与某台设备的连接，那么执行发现操作可能会大幅减少可用于该连接的带宽，
     *  因此不应该在处于连接状态时执行发现操作
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Add the name and address to an array adapter to show in a ListView
            Log.i(TAG, "onReceive: device name : \n " + device.getName()
                    + " address : \n"+ device.getAddress());
//            list.add(device);
//            Log.i(TAG, "onReceive: device size is " + list.size());
            lister.getDate(device);
        }
    }
}
