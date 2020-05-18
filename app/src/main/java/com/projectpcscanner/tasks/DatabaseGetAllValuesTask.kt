package com.projectpcscanner.tasks

import android.content.Context
import android.os.AsyncTask
import com.projectpcscanner.database.PCScannerDatabase
import com.projectpcscanner.database.entities.StaticsValueEntity

class DatabaseGetAllValuesTask(private val listener: Listener): AsyncTask<Void, Void, List<StaticsValueEntity>>() {
    interface Listener {
        fun getContext(): Context
        fun afterDatabaseQuery(list: List<StaticsValueEntity>)
    }
    override fun doInBackground(vararg params: Void?): List<StaticsValueEntity> {
        val pcScannerDatabase = PCScannerDatabase.getInstance(listener.getContext())
        return pcScannerDatabase!!.staticsValueDao().selectAll()
    }

    override fun onPostExecute(result: List<StaticsValueEntity>?) {
        super.onPostExecute(result)
        listener.afterDatabaseQuery(result!!)
    }
}