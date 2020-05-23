package com.projectpcscanner.tasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.projectpcscanner.database.PCScannerDatabase
import com.projectpcscanner.database.entities.StaticsDataEntity

class DatabaseGetDataTask(private val listener: Listener): AsyncTask<String, Void, StaticsDataEntity>() {
    interface Listener {
        fun getContext(): Context
        fun afterDatabaseQuery(data: StaticsDataEntity)
    }
    override fun doInBackground(vararg params: String?): StaticsDataEntity {
        val pcScannerDatabase = PCScannerDatabase.getInstance(listener.getContext())
        return pcScannerDatabase!!.staticsDataDao().getData(params[0]!!)
    }

    override fun onPostExecute(result: StaticsDataEntity?) {
        Log.d("data_returned", result.toString())
        super.onPostExecute(result)
        listener.afterDatabaseQuery(result!!)
    }
}