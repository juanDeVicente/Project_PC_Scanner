package com.projectpcscanner.tasks

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedInputStream
import java.io.InputStream
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

/**
 * No me convence el uso de Volley.RequestQueue por que cachea las peticiones http (?)
 */
abstract class RequestTask : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String?): String {
        val url = URL("${params[0]}:${params[1]}/${params[2]}")
        val response: String
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            response = String(urlConnection.inputStream.readBytes())
        }
        finally {
            urlConnection.disconnect()
        }
        return response
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onRequestComplete(result)
    }

    /**
     * Para obligar al que use esta clase a hacer algo con los datos devueltos del servidor, se proporciona este metodo abstracto para que se tenga que definir
     */
    abstract fun onRequestComplete(rawResults: String?)
}