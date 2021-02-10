package com.example.bluetoothle


import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(val userList: ArrayList<BTLEDevice>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.le_devices_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        holder.bindItems(userList[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: BTLEDevice) {
            val textViewName = itemView.findViewById(R.id.lblDeviceName) as TextView
            val textViewRSSI = itemView.findViewById(R.id.lblDeviceRSSI) as TextView
            val textViewMAC = itemView.findViewById(R.id.lblDeviceMAC) as TextView

            textViewMAC.text = "Name    :" + if (user.BTDevice.name != null ) user.BTDevice.name.toString() else  "Name Not Found"
            textViewRSSI.text = "address :" +user.BTDevice.address.toString()
            textViewName.text ="........................."
        }
    }
}