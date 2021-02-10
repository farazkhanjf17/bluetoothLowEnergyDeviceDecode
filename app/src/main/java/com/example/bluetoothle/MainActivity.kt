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
import java.nio.charset.StandardCharsets.UTF_16
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var scannerbt : scannerBTLE? = null
    var deviceList: ArrayList<BTLEDevice> = ArrayList<BTLEDevice>()
    var recyclerView : RecyclerView? = null
    var mBluetoothAdapter : BluetoothAdapter? = null
    var mBluetoothManager : BluetoothManager? = null
    var mBluetoothGatt: BluetoothGatt? = null
    var mDescriptor : BluetoothGattDescriptor? = null

    var characteristicsB : BluetoothGattCharacteristic? = null
    var characteristicsA : BluetoothGattCharacteristic? = null

    var serviceFour        : String = "6e400001-b5a3-f393-e0a9-e50e24dcca9f"
    var characteristicsZero : String = "6e400002-b5a3-f393-e0a9-e50e24dcca9f"
    var characteristicsOne : String = "6e400003-b5a3-f393-e0a9-e50e24dcca9f"

    val commandToWrite = byteArrayOf(0x07.toByte(), 0x01.toByte(), 0x01.toByte(), 0xf7.toByte())
    val commandToWrite1 = byteArrayOf(0x07.toByte(), 0x01.toByte(), 0x01.toByte(), 0xf7.toByte())
    val commandToWrite2 = byteArrayOf(0x07.toByte(), 0x01.toByte(), 0x01.toByte(), 0xf7.toByte())
    val commandToWrite3 = byteArrayOf(0x07.toByte(), 0x01.toByte(), 0x01.toByte(), 0xf7.toByte())
    val commandToWrite4 = byteArrayOf(0x07.toByte(), 0x01.toByte(), 0x01.toByte(), 0xf7.toByte())

    var servicesList: ArrayList<BluetoothGattService> = ArrayList<BluetoothGattService>()
    var Characteristicslist = mutableListOf<BluetoothGattCharacteristic>()

    var goodToWrite : Boolean = false

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
        scannerbt = scannerBTLE(this, 40000, -100)
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
//        for (btDevice : BTLEDevice in deviceList.distinct())
//        {
//            Log.e("devices Found", btDevice.BTDevice.toString())
//            Log.e("devices Found", btDevice.RSSI.toString())
//        }

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

            var characteristicsB : BluetoothGattCharacteristic? = null

            servicesList  = gatt?.services as ArrayList<BluetoothGattService>

            if (servicesList.get(3).uuid == UUID.fromString(serviceFour))
            {
                Characteristicslist = servicesList.get(3).characteristics
                characteristicsB = Characteristicslist.get(1)
                characteristicsA  = Characteristicslist.get(0)

                if (characteristicsB!!.uuid.equals(  UUID.fromString(characteristicsOne) ))
                {
                    //mBluetoothGatt!!.readCharacteristic(c)
                    mBluetoothGatt!!.setCharacteristicNotification(characteristicsB, true)

                    mDescriptor = characteristicsB.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                    mDescriptor!!.value =BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    mBluetoothGatt!!.writeDescriptor(mDescriptor)
                    //mBluetoothGatt!!.requestMtu(517)  // Maximum transfer unit


                   // mBluetoothGatt!!.disconnect()

                }
            }
        }

        override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)

            Log.e("descriptiorRead", descriptor!!.uuid.toString())
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
           Log.e("DESCRIPTOR WRITTEN", descriptor.toString())

            if(servicesList != null && Characteristicslist != null) {
                characteristicsA!!.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                characteristicsA!!.value = commandToWrite
                mBluetoothGatt!!.writeCharacteristic(characteristicsA)
                Log.e("Characteristics", characteristicsA!!.uuid.toString())
            }
            else
            {
                Log.e("Characteristics", "or service is Null")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.e("dataReceivedBt", Arrays.toString(characteristic!!.value))

        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.e("CHARACTERISTICS WRITTEN", "anything "+ if( Arrays.toString(characteristic!!.value) != null) "Null values " else " not null")
        }
    }


    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, payload: ByteArray) {
        mBluetoothGatt?.let { gatt ->
            //characteristic.writeType = writeType
            characteristic.writeType = BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
            characteristic.value = payload
            gatt.writeCharacteristic(characteristic)
        } ?: error("Not connected to a BLE device!")
    }
}