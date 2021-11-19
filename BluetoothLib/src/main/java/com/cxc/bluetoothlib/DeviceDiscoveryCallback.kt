package com.cxc.bluetoothlib

import android.bluetooth.BluetoothDevice

/**
 * @author caixingcun
 * @date 2021/11/19
 * Description : 设备搜索回调
 */
interface DeviceDiscoveryCallback {
    /**
     * 开启设备搜索
     */
    fun startDiscovery()

    /**
     * 发现新设备
     * @param bluetoothDevice 设备Device
     */
    fun found(bluetoothDevice: BluetoothDevice)

    /**
     * 绑定状态变化设备
     * @param bluetoothDevice 设备Device
     */
    fun boundStateChanged(bluetoothDevice: BluetoothDevice)

    /**
     * 查找完成
     * @param devices 设备Devices
     */
    fun discoveryFinished(devices: Set<BluetoothDevice>)
}
