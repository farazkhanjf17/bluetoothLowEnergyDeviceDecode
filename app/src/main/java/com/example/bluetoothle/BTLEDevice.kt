package com.example.bluetoothle

import android.bluetooth.BluetoothDevice

class BTLEDevice (  var BTDevice :BluetoothDevice , var RSSI : Int ) {

 fun equalss(BTDevice: BluetoothDevice, RSSI: Int): Boolean {
        return this.BTDevice === BTDevice && this.RSSI == RSSI
    }

    override fun equals(other: Any?): Boolean {
        var btleDevice : BTLEDevice =   other as BTLEDevice

        return this.BTDevice.toString() == btleDevice.toString() && this.RSSI.toString() == btleDevice.RSSI.toString()
    }
}