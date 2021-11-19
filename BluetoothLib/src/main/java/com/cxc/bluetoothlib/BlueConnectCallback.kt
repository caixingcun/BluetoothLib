package com.cxc.bluetoothlib

import java.io.IOException

/**
 * 连接连接回调
 */
interface BlueConnectCallback {
    /**
     * 开始开始
     */
    fun startConnect()

    /**
     * 连接成功
     */
    fun connectSuccess()

    /**
     * 连接失败
     * @param e 异常
     */
    fun connectFail(e: IOException)

    /**
     * 断开连接
     */
    fun disconnect()

    /**
     * 接收数据
     */
    fun receive(num:Int,bytes: ByteArray)

    /**
     * 接收数据失败
     * @param e 异常
     */
    fun receiveError(e: IOException)

    /**
     * 发送数据成功
     */
    fun sendSuccess()

    /**
     * 发送数据失败
     * @param e 异常
     */
    fun sendError(e:IOException)
}