package com.projectpcscanner.tasks

import android.content.Context
import android.os.AsyncTask
import com.projectpcscanner.database.PCScannerDatabase
import com.projectpcscanner.database.entities.StaticsDataEntity
import org.json.JSONArray
import org.json.JSONObject

class DatabaseDataTask(private val context: Context): AsyncTask<JSONArray, Void, Void>() {
    override fun doInBackground(vararg params: JSONArray?): Void? {
        val pcScannerDatabase = PCScannerDatabase.getInstance(context)!!
        val jsonObject = params[0]!!

        for (i in 0 until jsonObject.length()) {
            val jsonData = jsonObject.getJSONObject(i)
            pcScannerDatabase.staticsDataDao().insertAll(StaticsDataEntity(jsonData.getString("Name"), jsonData.getDouble("MaxValue").toFloat(), jsonData.getDouble("MinValue").toFloat(), jsonData.getDouble("ScalingFactor").toFloat()))
        }
        return null
    }
}