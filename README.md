# BluetoothLib
bluetooth connect format , a easy way to connect bluetooth socket


# dependence

app build.gradle
~~~
dependencies {
     implementation 'com.github.caixingcun:BluetoothLib:1.2'
}
~~~
project build.gradle 
~~~
      repositories {
        maven { url 'https://jitpack.io' }
     }
~~~


#desc
first you need require permission by yourself
second you can use BluetoothDiscoveryService to discovery devices 
third  you can use BlueConnectThread to connect socket by use discovered device you discovered in second step 

# example 
you can use like code in demo

1. request necessary permission 
~~~
    //this mush be define as a member field 
    var launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.forEach { pair ->
            if (!pair.value) {
                toast("请同意权限 ${pair.key}")
                return@registerForActivityResult
            }
        }
    }
    
    var launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.forEach { pair ->
            if (!pair.value) {
                toast("请同意权限 ${pair.key}")
                return@registerForActivityResult
            }
        }
    }
~~~

2. judge whether has bluetooth device function
~~~
     BluetoothDiscoveryService.getInstance().hasBlueToothDevice() 
~~~

3. judge whether has open bluetooth 
~~~
    BluetoothDiscoveryService.getInstance().blueToothIsEnable()
~~~

4. get  devices has binding
~~~
    val pairDevices = BluetoothDiscoveryService.getInstance().getBoundDevice()
                    pairDevices?.forEach {
                        log("boundStateChanged ${it.name} ${it.address}")
                    }
~~~

5. search surrounding devices

~~~
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
~~~

6.cancel discovery 
~~~
      BluetoothDiscoveryService.getInstance().stopDiscovery()
~~~    

7. connect choose discovery 
    
~~~
    //this mush be define as a member field 
   var connectThread: BlueConnectThread? = null
   
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
~~~

8. disconnect device

~~~
    connectThread?.release()
                    connectThread == null
~~~

9. send message 
~~~
                    connectThread?.send("hello".toByteArray())
~~~

