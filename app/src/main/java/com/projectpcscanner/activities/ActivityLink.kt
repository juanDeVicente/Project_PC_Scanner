package com.projectpcscanner.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.projectpcscanner.R
import com.projectpcscanner.fragments.dialogs.DialogFragmentNetworkError
import com.projectpcscanner.fragments.dialogs.DialogIntroduceIP
import com.projectpcscanner.tasks.BroadcastTask
import com.projectpcscanner.tasks.HelloTask
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.exitApplication
import com.projectpcscanner.utils.setActivityFullScreen


class ActivityLink : AppCompatActivity(), BroadcastTask.BroadcastTaskListener, DialogFragmentNetworkError.DialogFragmentNetworkErrorListener, DialogIntroduceIP.Listener, RequestTask.RequestTaskListener{
    /**
     * Variable que controla si se puede ir hacia atrás en la actividad
     */
    private var back: Boolean = true
    private var internetPermissions: Int = 2000
    private lateinit var ip: String
    private lateinit var port: String

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

    override fun afterBroadcast(address: String, port: String) {
        Log.d("IP", address)
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return
        with(sharedPreferences.edit()) {
            putString("address", address)
            putString("port", port)
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
        val dialogFragmentNetworkError = DialogFragmentNetworkError()
        dialogFragmentNetworkError.show(supportFragmentManager, "networkError")

        enableUI()
    }

    private fun startLink() {
        val broadcastTask = BroadcastTask(this)
        broadcastTask.execute()

        disableUI()
    }

    private fun disableUI() {
        back = false
        val linkButton: Button = findViewById(R.id.linkButton)
        linkButton.isEnabled = false
        linkButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.disableButton)
        val progressbar: ProgressBar = findViewById(R.id.linkProgressBar)
        progressbar.visibility = View.VISIBLE
    }

    private fun enableUI() {
        back = true
        val linkButton: Button = findViewById(R.id.linkButton)
        linkButton.isEnabled = true
        linkButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        val progressbar: ProgressBar = findViewById(R.id.linkProgressBar)
        progressbar.visibility = View.INVISIBLE
    }

    override fun onPositiveButton() {
        startLink()
    }

    override fun onNeutralButton() {
        val introduceIP = DialogIntroduceIP(this)
        introduceIP.show(supportFragmentManager, "introduceIP")
    }

    override fun onNegativeButton() {
        exitApplication(this)
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun retryConnection(ip: String, port: String) {
        disableUI()
        val requestTask = RequestTask(this)
        this.ip = ip
        this.port = port
        requestTask.execute("http://${ip}", port, "")
    }

    override fun afterRequest(rawData: String, tag: String) {
        Log.d("@@@@", "hola")
        afterBroadcast(ip, port)
    }

    override fun requestError() {
        Log.d("@@@@", "adios")
        enableUI()
    }
}
