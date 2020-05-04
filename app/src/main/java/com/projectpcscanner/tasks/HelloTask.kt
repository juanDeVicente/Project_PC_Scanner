package com.projectpcscanner.tasks

import android.os.AsyncTask
import android.util.Log
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

class HelloTask(private val listener: HelloTaskListener): AsyncTask<String, Void, Boolean>() {
    private lateinit var socket: DatagramSocket
    override fun doInBackground(vararg params: String?): Boolean {
        val url = params[0]
        socket = DatagramSocket(5002)
        socket.broadcast = true
        var packet = DatagramPacket(
            "".toByteArray(), 0,
            InetAddress.getByName(url), 5000
        )
        socket.send(packet)
        if (isCancelled)
            return false
        val buf = ByteArray(1024)
        packet = DatagramPacket(buf, buf.size)
        socket.soTimeout = 2000
        try {
            socket.receive(packet)
        }
        catch (e: SocketTimeoutException)
        {
            return false
        }
        socket.close()
        return true
    }

    override fun onCancelled() {
        try {
            socket.close()
        }
        catch (e: Exception)
        {

        }
        super.onCancelled()
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result == true)
            listener.afterHello()
    }

    interface HelloTaskListener{
        fun afterHello()
    }
}