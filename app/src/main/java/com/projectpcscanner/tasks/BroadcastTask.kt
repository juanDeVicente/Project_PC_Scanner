package com.projectpcscanner.tasks

import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class BroadcastTask(private val context: Context): AsyncTask<Void, Void, Boolean>() {
    override fun doInBackground(vararg params: Void?): Boolean {
        val socket = DatagramSocket(5000)
        socket.broadcast = true
        var packet = DatagramPacket(
            "".toByteArray(), 0,
            getBroadcastAddress(), 5000
        )
        socket.send(packet)

        val buf = ByteArray(1024)
        packet = DatagramPacket(buf, buf.size)
        socket.receive(packet)
        Log.d("PS", packet.address.hostAddress)

        return true
    }
    @Throws(IOException::class)
    fun getBroadcastAddress(): InetAddress? {
        val wifi: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifi.dhcpInfo
        val broadcast = info.ipAddress and info.netmask or info.netmask.inv()
        val quads = ByteArray(4)
        for (k in 0..3) quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
        return InetAddress.getByAddress(quads)
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result == true)
        {
            Log.d("TEST", "Ha pasado algo!")
        }

    }
}