package com.projectpcscanner.activities

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import com.projectpcscanner.R
import com.projectpcscanner.models.StaticsModel
import com.projectpcscanner.recycleviewadapters.DetailsRecycleViewAdapter
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.setActivityFullScreen
import com.projectpcscanner.utils.toMap
import com.vaibhavlakhera.circularprogressview.CircularProgressView
import org.json.JSONObject

class ActivityStaticDetail : AppCompatActivity(), RequestTask.RequestTaskListener {
    private lateinit var progressView: CircularProgressView
    private lateinit var detailAdapter: DetailsRecycleViewAdapter

    private lateinit var model: StaticsModel

    private val handler = Handler()
    private val delay = 1000
    private val staticsTag = "STATICS"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_static_detail)

        val details = mutableMapOf<String, String>()
        val detailsName = intent.getStringArrayListExtra("detailName")
        val detailsValue = intent.getStringArrayListExtra("detailValue")

        for (i in detailsName!!.indices)
            details[detailsName[i]] = detailsValue!![i]

        model = StaticsModel(
            intent.getStringExtra("name")!!,
            intent.getFloatExtra("currentValue", 0f),
            intent.getFloatExtra("minValue", 0f),
            intent.getFloatExtra("maxValue", 0f),
            intent.getFloatExtra("scalingFactor", 0f),
            intent.getStringExtra("measurementUnit")!!,
            details
        )

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
        val headerTextView = findViewById<TextView>(R.id.staticDetailHeader)
        headerTextView.text = model.name
        setUpView(false)
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        startRequestStatics()
    }
    private fun startRequestStatics() {
        Log.d("requestStatics", "He sido llamado")
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return
        val address = sharedPreferences.getString("address", null)
        handler.postDelayed(object : Runnable {
            override fun run() {
                val staticsRequestTask = RequestTask(this@ActivityStaticDetail)
                staticsRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", "5000", "statics", staticsTag)
                Log.d("Handler", "Ejecuto")
                handler.postDelayed(this, delay.toLong())
            }
        }, 0) //0 por que al principio quiero que se mande una petición para obtener los datos
    }

    override fun afterRequest(rawData: String, tag: String) {
        if (tag == staticsTag)
        {
            val jsonObject = JSONObject(rawData)
            for (i in 0 until jsonObject.getJSONArray("elements").length()) {
                val jsonData = jsonObject.getJSONArray("elements").getJSONObject(i)
                if ( jsonData.getString("name") == model.name)
                {
                    model.currentValue =  jsonData.getDouble("current_value").toFloat()
                    model.details = toMap(jsonData.getJSONObject("details")) as Map<String, String>
                    setUpView(true)
                    break
                }
            }
        }
    }

    private fun setUpView(animate: Boolean)
    {
        val currentValue = (100 * model.currentValue/model.maxValue).toInt()

        progressView = findViewById(R.id.staticDetailProgressView)
        progressView.setTotal(100)
        progressView.setProgress(currentValue, animate)
        progressView.setOnClickListener {
            dispatchKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_BACK
                )
            )
            dispatchKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_UP,
                    KeyEvent.KEYCODE_BACK
                )
            )
        }

        //Android es idiota
        val currentValueTextView = findViewById<TextView>(R.id.currentValueDisplay)
        var text = "${String.format("%.2f", model.currentValue/model.scalingFactor)} ${model.measurementUnit}"
        currentValueTextView.text = text

        //Android es idiota
        val maxValueTextView = findViewById<TextView>(R.id.maxValueDisplay)
        text = "${String.format("%.2f", model.maxValue/model.scalingFactor)} ${model.measurementUnit  }"
        maxValueTextView.text = text

        val detailRecyclerView = findViewById<RecyclerView>(R.id.detailRecyclerView)

        val layoutManager = LinearLayoutManager(this)
        detailRecyclerView.layoutManager = layoutManager

        detailAdapter = DetailsRecycleViewAdapter(model.details)
        detailRecyclerView.adapter = detailAdapter
    }

}
