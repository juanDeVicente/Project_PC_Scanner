package com.projectpcscanner.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.projectpcscanner.R
import com.projectpcscanner.utils.setActivityFullScreen

class ActivityHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_home)
    }
}
