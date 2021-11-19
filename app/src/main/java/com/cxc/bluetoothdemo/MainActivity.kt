package com.cxc.bluetoothdemo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.cxc.bluetoothlib.BlueConnectCallback
import com.cxc.bluetoothlib.BlueConnectThread
import com.cxc.bluetoothlib.BluetoothDiscoveryService
import com.cxc.bluetoothlib.DeviceDiscoveryCallback
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var lv: ListView

    companion object {
        const val REQUEST_ENABLE_BT = 200
    }

    var launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.forEach { pair ->
            if (!pair.value) {
                toast("请同意权限 ${pair.key}")
                return@registerForActivityResult
            }
        }
    }

    var connectThread: BlueConnectThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lv = findViewById<ListView>(R.id.lv)
        var array = resources.getStringArray(R.array.action)
        lv.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array)
        lv.setOnItemClickListener { _, _, position, _ ->
            when (array[position]) {
                "请求必要权限" -> {
                    launcher.launch(
                        arrayOf(
                            android.Manifest.permission.BLUETOOTH,
                            android.Manifest.permission.BLUETOOTH_ADMIN,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
                "判断是否有蓝牙设备" -> {
                    toast(
                        "蓝牙设备:${
                            if (BluetoothDiscoveryService.getInstance().hasBlueToothDevice()) {
                                "有"
                            } else {
                                "无"
                            }
                        }"
                    )
                }
                "判断是否已开启蓝牙设备" -> {
                    toast(
                        "已开启蓝牙:${
                            if (BluetoothDiscoveryService.getInstance().blueToothIsEnable()) {
                                "是"
                            } else {
                                "否"
                            }
                        }"
                    )
                }
                "开启蓝牙设备" -> {
                    BluetoothDiscoveryService.getInstance().enableBlueTooth(this, REQUEST_ENABLE_BT)
                }
                "获取绑定设备" -> {
                    val pairDevices = BluetoothDiscoveryService.getInstance().getBoundDevice()
                    pairDevices?.forEach {
                        log("boundStateChanged ${it.name} ${it.address}")
                    }
                }
                "搜索周边设备" -> {
                    BluetoothDiscoveryService.getInstance()
                        .discoveryDevice(this, this.lifecycle, object : DeviceDiscoveryCallback {
                            override fun startDiscovery() {
                                log("startDiscovery")
                            }

                            override fun found(bluetoothDevice: BluetoothDevice) {
                                log("found ${bluetoothDevice.name} ${bluetoothDevice.address}")
                            }

                            override fun boundStateChanged(bluetoothDevice: BluetoothDevice) {
                                log("boundStateChanged ${bluetoothDevice.name} ${bluetoothDevice.address}")
                            }

                            override fun discoveryFinished(devices: Set<BluetoothDevice>) {
                                devices.forEach {
                                    log("boundStateChanged ${it.name} ${it.address}")
                                }
                            }

                        })
                }

                "取消搜索" -> {
                    BluetoothDiscoveryService.getInstance().stopDiscovery()
                }
                "连接选中设备" -> {
                    connectThread =
                        BlueConnectThread("00:1A:7D:DA:71:13", object : BlueConnectCallback {
                            override fun startConnect() {
                                log("startConnect")
                            }

                            override fun connectSuccess() {
                                log("connectSuccess")
                            }

                            override fun connectFail(e: IOException) {
                                log("connectFail")
                            }

                            override fun disconnect() {
                                log("connectFail")
                            }

                            override fun receive(num: Int, bytes: ByteArray) {
                                var result = ByteArray(num)
                                for (index in result.indices) {
                                    result[index] = bytes[index]
                                }
                                log("receive ${String(result)}")
                            }

                            override fun receiveError(e: IOException) {
                                log("receiveError")
                            }

                            override fun sendSuccess() {
                                log("sendSuccess")
                            }

                            override fun sendError(e: IOException) {
                                log("sendError")
                            }

                        })
                    connectThread?.start()
                }
                "断开连接设备" -> {
                    connectThread?.release()
                    connectThread == null
                }
                "发送消息" -> {
                    val sdf = SimpleDateFormat("yyyyMMdd HH:mm:ss")
                    connectThread?.send("${sdf.format(Date())}".toByteArray())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            toast("蓝牙已开启")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectThread?.release()
    }

    val handler = Handler(Looper.getMainLooper())

    fun toast(msg: String) {
        handler.post {
            Toast.makeText(BlueDemoActivity@ this, msg, Toast.LENGTH_LONG).show()
        }
    }

    fun log(msg: String) {
        Log.d("tag", msg)
    }
}