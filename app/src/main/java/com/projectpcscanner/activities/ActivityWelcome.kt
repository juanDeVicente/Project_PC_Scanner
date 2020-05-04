package com.projectpcscanner.activities

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.projectpcscanner.R
import com.projectpcscanner.utils.setActivityFullScreen

class ActivityWelcome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_main)

        val nextButton: FloatingActionButton = findViewById(R.id.nextFloatingButton)
        nextButton.setOnClickListener{
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
}
