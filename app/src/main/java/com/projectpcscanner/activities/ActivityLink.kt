package com.projectpcscanner.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.projectpcscanner.R
import com.projectpcscanner.tasks.BroadcastTask
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.setActivityFullScreen


class ActivityLink : AppCompatActivity() {
    /**
     * Variable que controla si se puede ir hacia atrás en la actividad
     */
    private var back: Boolean = true
    private var internetPermissions: Int = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_link)

        val linkButton: Button = findViewById(R.id.linkButton)
        linkButton.setOnClickListener {
            val permissionCheck: Int =
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                startLink()
            else
                ActivityCompat.requestPermissions(
                    this,
                    Array(1) { Manifest.permission.INTERNET },
                    internetPermissions
                )
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {
        if (back)
            super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            internetPermissions -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    startLink()
                return
            }
            else -> {
                //Que raro es esto la vdd
            }
        }
    }

    private fun startLink() {
        back = false
        val linkButton: Button = findViewById(R.id.linkButton)
        linkButton.isEnabled = false
        val progressbar: ProgressBar = findViewById(R.id.linkProgressBar)
        progressbar.visibility = View.VISIBLE

        @SuppressLint("StaticFieldLeak") // Hasta los cojones de android hostia, se lo que hago
        val requestTask = object : RequestTask() {
            override fun onRequestComplete(rawResults: String?) {
                Log.d("Debug", rawResults)
            }
        }
        //requestTask.execute("http://192.168.1.254", "5000", "statics")

        val broadcastTask = BroadcastTask(this)
        broadcastTask.execute()
    }
}
