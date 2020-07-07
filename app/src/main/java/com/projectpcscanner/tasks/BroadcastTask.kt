package com.projectpcscanner.tasks

import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import com.projectpcscanner.utils.getBroadcastAddress
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketTimeoutException


class BroadcastTask(private val listener: BroadcastTaskListener): AsyncTask<Void, Void, List<String>>() {
    private lateinit var socket: DatagramSocket
    override fun doInBackground(vararg params: Void?): List<String>? {
        var packet: DatagramPacket? = null

        socket = DatagramSocket(5001)

        val message = listOf<String>(getDeviceName()!!, Build.VERSION.RELEASE, Build.VERSION.SDK_INT.toString()).joinToString()
        packet = DatagramPacket(
            message.toByteArray(), message.length,
            getBroadcastAddress(listener.getApplicationContext()), 5000
        )
        socket.send(packet)

        val buf = ByteArray(1024)
        packet = DatagramPacket(buf, buf.size)
        socket.soTimeout = 5000
        try {
            socket.receive(packet)
        } catch (e: SocketTimeoutException) {
            socket.close()
            return null
        }
        socket.close()
        return listOf(String(packet.data, 0, packet.length), packet.address.hostAddress)

    }

    override fun onPostExecute(result: List<String>?) {
        super.onPostExecute(result)
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
        if (s == null || s.isEmpty()) {
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