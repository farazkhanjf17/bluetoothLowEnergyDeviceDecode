package com.example.bluetoothle

import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.and


class MainActivity : AppCompatActivity() {

    var scannerbt : scannerBTLE? = null
    var deviceList: ArrayList<BTLEDevice> = ArrayList<BTLEDevice>()
    var servicesList: ArrayList<BluetoothGattService> = ArrayList<BluetoothGattService>()
    var recyclerView : RecyclerView? = null
    var mBluetoothAdapter : BluetoothAdapter? = null
    var mBluetoothManager : BluetoothManager? = null
    var mBluetoothGatt: BluetoothGatt? = null
    var mDescriptor : BluetoothGattDescriptor? = null
    var address : String = "78:04:73:C3:33:10"
    var addressPillbox : String = "E0:80:9E:1C:63:45"
    var serviceURIRequiired : String = "cdeacd80-5235-4c07-8846-93a37ee6b86d"


    var tv : TextView? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById(R.id.tvReadings)
        recyclerView = findViewById(R.id.rvDevices) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        this.mBluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        this.mBluetoothAdapter = mBluetoothManager!!.adapter

        setUpBTLEScanner()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setUpBTLEScanner() {
        scannerbt = scannerBTLE(this, 5000, -100)
        scannerbt!!.intializeScanner()
        scannerbt!!.start()
    }

    fun addDevices(device: BluetoothDevice, rssi: Int) {

        val device: BluetoothDevice = mBluetoothAdapter!!.getRemoteDevice(device.address)
        mBluetoothGatt = device.connectGatt(this@MainActivity, true, BTGattCallback)
        mBluetoothGatt!!.discoverServices()


        if (!deviceList.contains(BTLEDevice(device, rssi)))
            deviceList.add(BTLEDevice(device, rssi))
    }

    fun printDistinct(){

        for (btDevice : BTLEDevice in deviceList.distinct())
        {
            Log.e("devices Found", btDevice.BTDevice.toString())
            Log.e("devices Found", btDevice.RSSI.toString())
        }
        val adapter = CustomAdapter(deviceList)
        recyclerView!!.adapter = adapter
    }


    private val BTGattCallback = object : BluetoothGattCallback()
    {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.e("connection status = ", status.toString())
            val isDiscoverable = gatt!!.discoverServices()

            when(newState){
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.e("connection status = ", status.toString() + "connected")
                    Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_LONG).show()
                }
                BluetoothProfile.STATE_CONNECTING -> {
                    Log.e("connection status = ", status.toString() + "connecting")
                    Toast.makeText(this@MainActivity, "connecting", Toast.LENGTH_LONG).show()
                }
                BluetoothProfile.STATE_DISCONNECTING -> {
                    Log.e("connection status = ", status.toString() + "Disconnecting")
                    Toast.makeText(this@MainActivity, "Disconnecting", Toast.LENGTH_LONG).show()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.e("connection status = ", status.toString() + "Disconnected")
                    Toast.makeText(this@MainActivity, "Disconnected", Toast.LENGTH_LONG).show()
                }
          }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            servicesList  = gatt?.services as ArrayList<BluetoothGattService>
            var i : Int = 0
            var j : Int = 0
            for (s in servicesList) {
                for (c in s.characteristics) {
                    Log.e("services : " + i.toString() + " = ", s.uuid.toString())
                    Log.e("characteristic : " + j.toString() + " = ", c.uuid.toString())
                  if(i==3 && j==1)
                  {
//                      mBluetoothGatt!!.readCharacteristic(c)
                      mBluetoothGatt!!.setCharacteristicNotification(c, true)

                      mDescriptor = c.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                      mDescriptor!!.value =BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                      mBluetoothGatt!!.writeDescriptor(mDescriptor)

                      Log.e("servicesRead ", s.uuid.toString())
                      Log.e("characteristicRead ", c.uuid.toString())
                  }
                j++
                }
                i++
                j=0
            }
        }


        override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)

            Log.e("descriptiorRead", descriptor!!.uuid.toString())
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
           Log.e("decWrite", descriptor.toString())

        }


        var i : Int? = null

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.e("dataReceivedBt",  Arrays.toString(characteristic!!.value) )

        }}
}