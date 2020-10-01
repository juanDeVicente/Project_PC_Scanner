package com.projectpcscanner.tasks

import android.os.AsyncTask
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AuthenticationTask(private val listener: Listener): AsyncTask<String, Unit, Boolean>() {
    private lateinit var tag: String

    override fun doInBackground(vararg params: String?): Boolean {
        tag = try {
            params[3]
        } catch (e: ArrayIndexOutOfBoundsException) {
            ""
        } as String

        val result: Boolean
        val url = URL("http://${params[0]}:${params[1]}/?password=${params[2]}")
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

        urlConnection.connectTimeout = 1000
        urlConnection.requestMethod = "POST"
        result = try {
            urlConnection.responseCode != 401
        } catch (e: Exception) {
            false
        } finally {
            urlConnection.disconnect()
        }
        return result
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result!!)
            listener.successAuthenticate()
        else
            listener.wrongAuthenticate()

    }
    interface Listener {
        fun successAuthenticate()
        fun wrongAuthenticate()
    }
}