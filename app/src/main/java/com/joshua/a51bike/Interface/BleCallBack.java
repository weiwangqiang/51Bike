package com.joshua.a51bike.Interface;

/**
 * ============================================================
 * <p>
 * 版 权 ： 吴奇俊  (c) 2017
 * <p>
 * 作 者 : 吴奇俊
 * <p>
 * 版 本 ： 1.0
 * <p>
 * 创建日期 ： 2017/4/20 21:31
 * <p>
 * 描 述 ：
 * <p>
 * ============================================================
 **/

public interface BleCallBack {
    /**
     * 判断Ble设备的连接结果
     */
    void onGattConnect(String action);
    /**
     * 向Ble设备发送数据后，判断Ble设备的状态
     */
    void getStateFromDevice(int state);
    /**
     * 向Ble设备发送数据后，判断是否需要继续发送
     * Ble设备每次只能接受20字节的数据
     */
    void onCharacteristicContinueWrite();
    /**
     * 向Ble设备发送完数据后，开始向设备读取数据
     */
    void onCharacteristicFinishWrite();
}
