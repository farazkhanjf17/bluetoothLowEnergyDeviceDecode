package com.example.bluetoothle

import android.bluetooth.*
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlin.contracts.contract


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
data class scannerBTLE(var mainActivity: MainActivity, var scanPeriod: Int, val signalStrength: Int) {


    val listFilter: MutableList<ScanFilter> = ArrayList()

    var mScanning : Boolean? = null
    var mHandler : Handler? = null
    var mBluetoothAdapter : BluetoothAdapter? = null
    var mBluetoothManager : BluetoothManager? = null
    private var mScanner: BluetoothLeScanner? = null
    //var address : String = "E0:80:9E:1C:63:45"
    var address : String = "78:04:73:C3:33:10"

    var mScanSettings : ScanSettings? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun intializeScanner()
    {
        mHandler = Handler()
        this.mBluetoothManager = mainActivity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        this.mBluetoothAdapter = mBluetoothManager!!.adapter
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getScanSettings()
            getScanFilters()
        }

    }
    fun isScanning () : Boolean {
        return mScanning!!
    }

    fun start()
    {
        if(mBluetoothAdapter!= null)
        {
            scanDevices(true)

        }
        else{ Toast.makeText(mainActivity.applicationContext, "Bluetooth is turned off", Toast.LENGTH_LONG).show()
        }
    }

    fun getScanFilters ()
    {

        val scanFilterMac = ScanFilter.Builder().setDeviceAddress(address).build()
        listFilter.add(scanFilterMac)

    }

    fun getScanSettings ()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
         mScanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
                    //.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)//TODO:to be verified on android 10
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                    .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                    //.setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                    .setReportDelay(0L)
                    .build()
        }
    }

    fun scanDevices(check: Boolean)
    {
//
//        val setting = ScanSettings
//                .s
        if(check )
        {
            Toast.makeText(mainActivity.applicationContext, "Bluetooth Scan is on", Toast.LENGTH_LONG).show()

            mHandler!!.postDelayed(Runnable {
                mScanning = false
                scanDevices(false)

            }, scanPeriod.toLong())

            mScanning = true
            mScanner = mBluetoothAdapter!!.bluetoothLeScanner
            mScanner!!.startScan(listFilter, mScanSettings, mScanCallback)
        }
        else{
            mScanner!!.stopScan(mScanCallback)
            mainActivity.printDistinct()
        }
    }

    private val mLeScanCallback =
        LeScanCallback { device, rssi, scanRecord ->

            if(rssi > signalStrength)
            {
                    mainActivity.addDevices(device, rssi)
            }

        }
    private val mScanCallback = object : ScanCallback()
    {
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            val device = result.device
            mainActivity.addDevices(device, callbackType)


        }
    }





}
