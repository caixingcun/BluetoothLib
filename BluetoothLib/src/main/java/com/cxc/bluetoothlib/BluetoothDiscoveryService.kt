package com.cxc.bluetoothlib

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.lang.Exception

/**
 * 蓝牙查找服务
 */
class BluetoothDiscoveryService {

    companion object {
        /**
         * 单例
         */
        private lateinit var service: BluetoothDiscoveryService

        fun getInstance(): BluetoothDiscoveryService {
            if (!this::service.isInitialized) {
                service = BluetoothDiscoveryService()
            }
            return service
        }
    }

    /**
     * 蓝牙主适配器
     */
    private var blueToothAdapter: BluetoothAdapter? = null
        get() = if (field != null) field else {
            field = BluetoothAdapter.getDefaultAdapter()
            field
        }

    /**
     * 查找回调
     */
    private var callback: DeviceDiscoveryCallback? = null

    /**
     * 查找的设备集合
     */
    private var devices = HashSet<BluetoothDevice>()

    /**
     * 上下文 用来 注册和反注册 广播接收
     */
    var context: Context? = null

    /**
     * 判断是否存在蓝牙设备
     * @return true 存在蓝牙设备 / false 不存在蓝牙设备
     */
    fun hasBlueToothDevice(): Boolean {
        if (blueToothAdapter == null) {
            blueToothAdapter = BluetoothAdapter.getDefaultAdapter()
        }
        return blueToothAdapter != null
    }

    /**
     * 蓝牙开启
     */
    fun enableBlueTooth(activity: AppCompatActivity,requestCode:Int) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBtIntent, requestCode)
    }

    /**
     * 判断蓝牙是否开启
     * @return true 已开启/ false 未开启
      */
    fun blueToothIsEnable():Boolean{
        if (blueToothAdapter == null) {
            return false
        }
        return blueToothAdapter!!.isEnabled
    }
    /**
     * 获取绑定设备
     * @return 绑定的设备集合
     */
    fun getBoundDevice(): Set<BluetoothDevice>? {
        if (blueToothAdapter != null) {
            return blueToothAdapter?.bondedDevices
        }
        return mutableSetOf()
    }

    /**
     * 查找设备
     * @param context 上下文
     * @param lifecycle 生命周期管理
     * @param callback 回调
     */
    fun discoveryDevice(
        context: Context,
        lifecycle: Lifecycle?,
        callback: DeviceDiscoveryCallback
    ) {
        this.callback = callback
        this.devices.clear()
        this.context = context
        //异步操作  执行设备查找 前需要先需要先cancelDiscovery 再建立连接
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(discoveryReceiver, intentFilter)
        blueToothAdapter?.let {
            if (it.isDiscovering) {
                it.cancelDiscovery()
            }
        }
        blueToothAdapter?.startDiscovery()
        this.callback?.startDiscovery()
        lifecycle?.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                try {
                    blueToothAdapter?.cancelDiscovery()
                    context.unregisterReceiver(discoveryReceiver)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * 暂停搜索设备
     */
    fun stopDiscovery() {
        try {
            blueToothAdapter?.cancelDiscovery()
            context?.unregisterReceiver(discoveryReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var action = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    var device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    devices.add(device)
                    callback?.found(device)
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    var device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    devices.add(device)
                    callback?.boundStateChanged(device)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    callback?.discoveryFinished(devices)
                }
            }
        }
    }

}