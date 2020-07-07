package com.projectpcscanner.tasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.projectpcscanner.utils.getBroadcastAddress
import java.net.DatagramPacket
import java.net.DatagramSocket

class WakeOnLanTask(private val listener: Listener): AsyncTask<String, Unit, Unit>() {
    interface Listener {
        fun getApplicationContext(): Context
    }

    override fun doInBackground(vararg params: String?) {
        if (params.isEmpty())
            return

        val socket = DatagramSocket()
        val macAddressBytes = getMacBytes(params[0]!!)

        val bytes = ByteArray(6 + 16 * macAddressBytes.size)
        for (i in 0..5) {
            bytes[i] = 0xff.toByte()
        }
        var i = 6
        while (i < bytes.size) {
            System.arraycopy(macAddressBytes, 0, bytes, i, macAddressBytes.size)
            i += macAddressBytes.size
        }


        val packet = DatagramPacket(
            bytes, bytes.size,
            getBroadcastAddress(listener.getApplicationContext()), 9
        )
        socket.send(packet)
        socket.close()
    }

    @Throws(IllegalArgumentException::class)
    private fun getMacBytes(mac: String): ByteArray {
        val bytes = ByteArray(6)
        require(mac.length == 12) { "Invalid MAC address..." }
        try {
            var hex: String
            for (i in 0..5) {
                Log.d("@@@", i.toString())
                hex = mac.substring(i * 2, i * 2 + 2)
                bytes[i] = hex.toInt(16).toByte()
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid hex digit...")
        }
        return bytes
    }
}