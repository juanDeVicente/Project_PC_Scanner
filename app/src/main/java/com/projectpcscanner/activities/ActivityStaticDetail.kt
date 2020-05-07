package com.projectpcscanner.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.transition.addListener
import androidx.transition.Transition
import com.projectpcscanner.R
import com.projectpcscanner.utils.setActivityFullScreen
import com.vaibhavlakhera.circularprogressview.CircularProgressView

class ActivityStaticDetail : AppCompatActivity() {
    private lateinit var progressView: CircularProgressView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_static_detail)

        //Android, que es muy comodo xd
        window.sharedElementEnterTransition.addListener (object: Transition.TransitionListener,
            android.transition.Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                progressView.setProgressTextEnabled(true)
                progressView.setAnimate(true)
            }
            override fun onTransitionResume(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionStart(transition: Transition) {}
            override fun onTransitionEnd(transition: android.transition.Transition?) {
                progressView.setProgressTextEnabled(true)
                progressView.setAnimate(true)
            }
            override fun onTransitionResume(transition: android.transition.Transition?) {}
            override fun onTransitionPause(transition: android.transition.Transition?) {}
            override fun onTransitionCancel(transition: android.transition.Transition?) {}
            override fun onTransitionStart(transition: android.transition.Transition?) {}

        })

        val currentValue = (100 * intent.getFloatExtra("currentValue", 0f)/intent.getFloatExtra("maxValue", 1f)).toInt()

        val headerTextView = findViewById<TextView>(R.id.staticDetailHeader)
        headerTextView.text = intent.getStringExtra("name")

        progressView = findViewById<CircularProgressView>(R.id.staticDetailProgressView)
        Log.d("receive_values", "${intent.getFloatExtra("maxValue", 0f)}, ${intent.getFloatExtra("currentValue", 0f)}")
        progressView.setTotal(100)
        progressView.setProgress(currentValue, false)

        //Android es idiota
        val currentValueTextView = findViewById<TextView>(R.id.staticDetailCurrentValue)
        var text = "${getString(R.string.current_value)}: ${intent.getFloatExtra("currentValue", 0f)}"
        currentValueTextView.text = text

        //Android es idiota
        val maxValueTextView = findViewById<TextView>(R.id.staticDetailMaxValue)
        text = "${getString(R.string.max_value)}: ${intent.getFloatExtra("maxValue", 0f)}"
        maxValueTextView.text = text
    }

}
