package com.projectpcscanner.tasks

import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class BroadcastTask(private val listener: BroadcastTaskListener): AsyncTask<Void, Void, String>() {
    private lateinit var socket: DatagramSocket
    override fun doInBackground(vararg params: Void?): String {
        socket = DatagramSocket(5001)
        socket.broadcast = true
        var packet = DatagramPacket(
            "".toByteArray(), 0,
            getBroadcastAddress(), 5000
        )
        socket.send(packet)

        val buf = ByteArray(1024)
        packet = DatagramPacket(buf, buf.size)
        socket.receive(packet)
        socket.close()
        return packet.address.hostAddress
    }
    @Throws(IOException::class)
    fun getBroadcastAddress(): InetAddress? {
        val wifi: WifiManager = listener.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifi.dhcpInfo
        val broadcast = info.ipAddress and info.netmask or info.netmask.inv()
        val quads = ByteArray(4)
        for (k in 0..3) quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
        return InetAddress.getByAddress(quads)
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        listener.afterBroadcast(result)

    }

    override fun onCancelled() {
        super.onCancelled()
        socket.close()
    }

    interface BroadcastTaskListener{
        fun afterBroadcast(address: String)
        fun getApplicationContext(): Context
    }
}