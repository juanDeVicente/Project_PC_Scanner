package com.projectpcscanner.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.projectpcscanner.R
import com.projectpcscanner.tasks.HelloTask
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.setActivityFullScreen

class ActivityWelcome : AppCompatActivity(), HelloTask.HelloTaskListener, RequestTask.RequestTaskListener {
    private lateinit var helloTask: HelloTask
    private lateinit var requestTask: RequestTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return
        val address = sharedPreferences.getString("address", null)

        if (address != null) {
            helloTask = HelloTask(this)
            requestTask = RequestTask(this)

            helloTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, address)
            requestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", "5000", "")
        }

        val nextButton: FloatingActionButton = findViewById(R.id.nextFloatingButton)
        nextButton.setOnClickListener{
            if (address != null)
            {
                helloTask.cancel(true)
                requestTask.cancel(true)
            }
            val intent = Intent(this, ActivityLink::class.java)
            startActivity(intent)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        try {
            val info: PackageInfo = applicationContext.packageManager.getPackageInfo(packageName, 0)
            val versionTextView: TextView = findViewById(R.id.versionTextView)
            versionTextView.text = info.versionName
        }
        catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun afterHello() {
        requestTask.cancel(true)
        val nextButton: FloatingActionButton = findViewById(R.id.nextFloatingButton)
        nextButton.setOnClickListener{
            val intent = Intent(this, ActivityHome::class.java)
            startActivity(intent)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }

    override fun afterRequest(rawData: String, tag: String) {
        helloTask.cancel(true)
        val nextButton: FloatingActionButton = findViewById(R.id.nextFloatingButton)
        nextButton.setOnClickListener{
            val intent = Intent(this, ActivityHome::class.java)
            startActivity(intent)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }
}
