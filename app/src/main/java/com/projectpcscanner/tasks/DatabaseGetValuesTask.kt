package com.projectpcscanner.tasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.projectpcscanner.database.PCScannerDatabase
import java.util.*

class DatabaseGetValuesTask(private val listener: Listener): AsyncTask<Date, Void, MutableList<Float>>() {

    interface Listener {
        fun afterDatabaseQuery(data: MutableList<Float>)
        fun getModelName(): String
        fun getContext(): Context
    }

    override fun doInBackground(vararg params: Date?): MutableList<Float> {
        val date = params[0]!!
        val pcScannerDatabase = PCScannerDatabase.getInstance(listener.getContext())
        val databaseValues = pcScannerDatabase!!.staticsValueDao().select(date, listener.getModelName())
        val size = databaseValues.size
        for (i in 0 until 20 - size)
            databaseValues.add(0.0f)
        return databaseValues.reversed().toMutableList()
    }

    override fun onPostExecute(result: MutableList<Float>?) {
        super.onPostExecute(result)
        Log.d("Datos de la BBDD", result.toString())
        listener.afterDatabaseQuery(result!!)
    }
}