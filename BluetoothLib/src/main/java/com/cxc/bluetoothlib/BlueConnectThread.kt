package com.cxc.bluetoothlib

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.*

/**
 * @author caixingcun
 * @date 2021/11/18
 * Description : 蓝牙连接服务
 */
class BlueConnectThread(
    var device: BluetoothDevice,
    var callback: BlueConnectCallback
) : Thread() {
    companion object {
        /**
         * 服务端id 固定
         */
        private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    constructor(address: String, callback: BlueConnectCallback) : this(
        BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address), callback
    )

    /**
     * 蓝牙连接后socket
     */
    private var socket: BluetoothSocket? = null

    /**
     * 数据接收缓冲区 1024字节
     */
    private var mmBuffer: ByteArray = ByteArray(1024)

    /**
     * 连接状态
     */
    var connected: Boolean = false

    override fun run() {
        callback.startConnect()
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid)
        } catch (e: IOException) {
            e.printStackTrace()
            callback.connectFail(e)
            return
        }
        socket?.let { socket ->
            try {
                socket.connect()
                connected = true
                callback.connectSuccess()
                while (connected) {
                    try {
                        val inputStream = socket.inputStream
                        val read = inputStream.read(mmBuffer)
                        callback.receive(read, mmBuffer)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        callback.receiveError(e)
                    }
                }
                callback.disconnect()
            } catch (e: IOException) {
                e.printStackTrace()
                connected = false
                callback.connectFail(e)
            }
        }
    }

    fun send(byte: ByteArray) {
        try {
            socket?.let { socket ->
                var outputStram = socket.outputStream
                outputStram.write(byte)
                outputStram.flush()
                callback.sendSuccess()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            callback.sendError(e)
        }
        return
    }

    fun release() {
        try {
            connected = false
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}