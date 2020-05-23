package com.projectpcscanner.tasks

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.projectpcscanner.database.PCScannerDatabase

class DatabaseDeleteTask(private val listener: Listener): AsyncTask<Unit, Unit, Unit>() {
    interface Listener {
        fun afterDeleteDatabase()
        fun getContext(): Context
    }
    override fun doInBackground(vararg params: Unit?) {
        val pcScannerDatabase = PCScannerDatabase.getInstance(listener.getContext())
        pcScannerDatabase!!.clearDatabase()
    }

    override fun onPostExecute(result: Unit?) {
        listener.afterDeleteDatabase()
        Log.d("delete", "database deleted")
        super.onPostExecute(result)
    }
}