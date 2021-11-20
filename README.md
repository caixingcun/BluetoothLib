# BluetoothLib
bluetooth connect format , a easy way to connect bluetooth socket


# dependence

app build.gradle
~~~
dependencies {
    implementation 'com.github.caixingcun:BluetoothLib:1.0'
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
