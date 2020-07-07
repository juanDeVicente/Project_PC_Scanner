package com.projectpcscanner.activities

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.projectpcscanner.R
import com.projectpcscanner.tasks.WakeOnLanTask
import com.projectpcscanner.utils.setActivityFullScreen

class ActivityWakeOnLan : AppCompatActivity(), WakeOnLanTask.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wake_on_lan)
        setActivityFullScreen(this)

        val toolbar: Toolbar = findViewById(R.id.wakeOnLANToolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val button: Button = findViewById(R.id.buttonWakeOnLan)

        val editText = findViewById<TextView>(R.id.editTextWakeOnLan)
        editText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val macText = s.toString()
                Log.d("@@@", macText)
                val regexVars = Regex("([0-9a-fA-F][0-9a-fA-F](-)?){5}[0-9a-fA-F][0-9a-fA-F]")
                val regexDots = Regex("([0-9a-fA-F][0-9a-fA-F]:){5}[0-9a-fA-F][0-9a-fA-F]")

                if (macText.matches(regexVars) || macText.matches(regexDots))
                {
                    button.isEnabled = true
                    button.backgroundTintList = ContextCompat.getColorStateList(this@ActivityWakeOnLan, R.color.colorPrimary)
                }
                else
                {
                    button.isEnabled = false
                    button.backgroundTintList = ContextCompat.getColorStateList(this@ActivityWakeOnLan, R.color.disableButton)
                }
            }
        })
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return
        val macAddress = sharedPreferences.getString("mac", "")
        editText.text = macAddress
        button.setOnClickListener {
            with(sharedPreferences.edit()) {
                putString("mac", editText.text.toString())
                apply()
            }
            val text = editText.text.toString().replace("-", "").replace(":", "")
            val task = WakeOnLanTask(this)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}
