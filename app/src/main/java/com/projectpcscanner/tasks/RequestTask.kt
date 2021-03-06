package com.projectpcscanner.tasks

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedInputStream
import java.io.InputStream
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

/**
 * No me convence el uso de Volley.RequestQueue por que cachea las peticiones http (?)
 */
class RequestTask(private val listener: RequestTaskListener) : AsyncTask<String, Void, String>() {
    private lateinit var tag: String

    override fun doInBackground(vararg params: String?): String? {
        tag = try {
            params[3]
        } catch (e: ArrayIndexOutOfBoundsException) {
            ""
        } as String

        val url = URL("${params[0]}:${params[1]}/${params[2]}")
        var response: String?
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        urlConnection.connectTimeout = 1000
        try {
            response = String(urlConnection.inputStream.readBytes())
        }
        catch (e: ConnectException)
        {
            response = null
        }
        catch (e: SocketTimeoutException)
        {
            response = null
        }
        catch (e: RuntimeException)
        {
            response = null
        }
        catch (e: Exception)
        {
            response = null
        }
        finally {
            urlConnection.disconnect()
        }
        return response
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result != null)
            listener.afterRequest(result, this.tag)
        else
            listener.requestError()
    }

    interface RequestTaskListener{
        fun afterRequest(rawData: String, tag: String)
        fun requestError() {}
    }
}