package com.projectpcscanner.tasks

import android.content.Context
import android.os.AsyncTask
import com.projectpcscanner.database.PCScannerDatabase
import com.projectpcscanner.database.entities.StaticsValueEntity
import org.json.JSONArray
import java.util.*

class DatabaseValueTask(private val context: Context): AsyncTask<JSONArray, Void, Void>() {
    override fun doInBackground(vararg params: JSONArray?): Void? {
        val pcScannerDatabase = PCScannerDatabase.getInstance(context)!!
        val jsonObject = params[0]!!
        val data = mutableListOf<StaticsValueEntity>()

        for (i in 0 until jsonObject.length()) {
            val jsonData = jsonObject.getJSONObject(i)
            data.add(StaticsValueEntity(0, jsonData.getString("name"), jsonData.getDouble("current_value").toFloat(), Date()))
        }

        pcScannerDatabase.staticsValueDao().insertAll(data)

        return null
    }
}