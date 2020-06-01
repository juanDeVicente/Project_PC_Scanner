package com.projectpcscanner.tasks

import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException


class BroadcastTask(private val listener: BroadcastTaskListener): AsyncTask<Void, Void, List<String>>() {
    private lateinit var socket: DatagramSocket
    override fun doInBackground(vararg params: Void?): List<String>? {
        var i = 0
        var packet: DatagramPacket? = null
        while (i < 5) { // Kotlin no permite usar una variable externa para iterar sobre un bucle for...
            socket = DatagramSocket(5001)

            val message = listOf<String>(getDeviceName()!!, Build.VERSION.RELEASE, Build.VERSION.SDK_INT.toString()).joinToString()
            packet = DatagramPacket(
                message.toByteArray(), message.length,
                getBroadcastAddress(), 5000
            )
            socket.send(packet)

            val buf = ByteArray(1024)
            packet = DatagramPacket(buf, buf.size)
            socket.soTimeout = 1000
            try {
                socket.receive(packet)
                break
            } catch (e: SocketTimeoutException) {
                i++
                socket.close()
            }
        }
        if (i < 5 && packet != null) {
            socket.close()
            return listOf(String(packet.data, 0, packet.length), packet.address.hostAddress)
        }
        return null
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

    override fun onPostExecute(result: List<String>?) {
        super.onPostExecute(result)
        Log.d("broadcast", result.toString())
        if (result != null)
            listener.afterBroadcast(result[1], result[0])
        else
            listener.errorBroadcast()
    }

    override fun onCancelled() {
        super.onCancelled()
        try {
            socket.close()
        }
        catch (e: Exception)
        {

        }
    }
    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }


    private fun capitalize(s: String?): String {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }
    interface BroadcastTaskListener{
        fun afterBroadcast(address: String, port: String)
        fun errorBroadcast()
        fun getApplicationContext(): Context
    }
}