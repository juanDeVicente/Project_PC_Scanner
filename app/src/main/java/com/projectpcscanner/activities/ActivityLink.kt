package com.projectpcscanner.activities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.projectpcscanner.R
import com.projectpcscanner.tasks.BroadcastTask
import com.projectpcscanner.utils.exitApplication
import com.projectpcscanner.utils.setActivityFullScreen


class ActivityLink : AppCompatActivity(), BroadcastTask.BroadcastTaskListener {
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

    override fun afterBroadcast(address: String) {
        Log.d("IP", address)
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return
        with(sharedPreferences.edit()) {
            putString("address", address)
            apply()
        }
        val intent = Intent(this, ActivityHome::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun errorBroadcast() {
        enableUI()

        val builder: AlertDialog.Builder? = let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("Reintentar") { _, _ ->
                    startLink()
                }
                setNegativeButton("Cerrar la app") { _, _ ->
                    exitApplication(this@ActivityLink)
                }
            }
        }
        builder?.setMessage("Parece que no ha sido posible establecer conexión con el servidor")?.setTitle("Error de conexión")
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

    private fun startLink() {
        disableUI()

        val broadcastTask = BroadcastTask(this)
        broadcastTask.execute()
    }

    private fun disableUI() {
        back = false
        val linkButton: Button = findViewById(R.id.linkButton)
        linkButton.isEnabled = false
        val progressbar: ProgressBar = findViewById(R.id.linkProgressBar)
        progressbar.visibility = View.VISIBLE
    }

    private fun enableUI() {
        back = true
        val linkButton: Button = findViewById(R.id.linkButton)
        linkButton.isEnabled = true
        val progressbar: ProgressBar = findViewById(R.id.linkProgressBar)
        progressbar.visibility = View.INVISIBLE
    }
}
