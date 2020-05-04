package com.projectpcscanner.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import com.projectpcscanner.R
import com.projectpcscanner.utils.exitApplication
import com.projectpcscanner.utils.setActivityFullScreen
import kotlin.system.exitProcess

class ActivityHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "PC Scanner"
        toolbar.navigationIcon = getDrawable(R.drawable.baseline_menu_24)
        setSupportActionBar(toolbar)
    }

    override fun onBackPressed() {
        exitApplication(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.welcome_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
