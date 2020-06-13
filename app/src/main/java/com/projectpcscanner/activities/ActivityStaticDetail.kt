package com.projectpcscanner.activities

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.projectpcscanner.R
import com.projectpcscanner.models.StaticsModel
import com.projectpcscanner.recycleviewadapters.DetailsRecycleViewAdapter
import com.projectpcscanner.tasks.DatabaseGetValuesTask
import com.projectpcscanner.tasks.DatabaseValueTask
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.setActivityFullScreen
import com.projectpcscanner.utils.toMap
import com.vaibhavlakhera.circularprogressview.CircularProgressView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ActivityStaticDetail : AppCompatActivity(), RequestTask.RequestTaskListener, DatabaseGetValuesTask.Listener {
    private lateinit var progressView: CircularProgressView
    private lateinit var detailAdapter: DetailsRecycleViewAdapter

    private lateinit var model: StaticsModel
    private lateinit var currentDate: Date

    private val handler = Handler()
    private val delay = 1000
    private val staticsTag = "STATICS"

    private lateinit var chart: LineChart

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

        currentDate = Date(intent.getLongExtra("date", System.currentTimeMillis()))

        val cl: ConstraintLayout = findViewById(R.id.activityStaticDetailConstraintLayout)
        val cs = ConstraintSet()
        cs.clone(cl)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            cs.setHorizontalBias(R.id.staticDetailVerticalDivider, 0.55f)
        else
            cs.setHorizontalBias(R.id.staticDetailVerticalDivider, 0.33f)
        cs.applyTo(cl)

        val params = findViewById<View>(R.id.detailRecyclerViewStaticDetail).layoutParams
        params.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38f * model.details.size, resources.displayMetrics).toInt()

        findViewById<View>(R.id.detailRecyclerViewStaticDetail).layoutParams = params
        findViewById<View>(R.id.detailRecyclerViewStaticDetail).requestLayout()

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

        chart = findViewById(R.id.chart)
        chart.setDrawGridBackground(false)

        chart.isDragEnabled = false
        chart.setScaleEnabled(false)

        chart.setDrawBorders(true)
        chart.setBorderColor(Color.WHITE)

        chart.description.isEnabled = false
        chart.isClickable = false

        var yAxis: YAxis
        run {
            yAxis = chart.axisLeft

            chart.axisRight.isEnabled = false

            yAxis.axisMaximum = model.maxValue/model.scalingFactor
            yAxis.axisMinimum = model.minValue
            yAxis.textColor = Color.WHITE
        }

        chart.legend.isEnabled = false
        chart.isHighlightPerDragEnabled = false
        chart.isHighlightPerTapEnabled = false
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
        val port = sharedPreferences.getString("port", "5000")
        handler.postDelayed(object : Runnable {
            override fun run() {
                val staticsRequestTask = RequestTask(this@ActivityStaticDetail)
                staticsRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", port, "statics", staticsTag)

                val databaseGetValuesTask = DatabaseGetValuesTask(this@ActivityStaticDetail)
                databaseGetValuesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentDate)

                Log.d("Handler", "Ejecuto")
                handler.postDelayed(this, delay.toLong())
            }
        }, 0) //0 por que al principio quiero que se mande una petición para obtener los datos
    }

    override fun afterRequest(rawData: String, tag: String) {
        if (tag == staticsTag)
        {
            val jsonObject = JSONArray(rawData)

            val databaseValueTask = DatabaseValueTask(this)
            databaseValueTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject)

            for (i in 0 until jsonObject.length()) {
                val jsonData = jsonObject.getJSONObject(i)
                if ( jsonData.getString("Name") == model.name)
                {
                    model.currentValue =  jsonData.getDouble("CurrentValue").toFloat()
                    model.details = toMap(jsonData.getJSONObject("Details")) as Map<String, String>
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

        val detailRecyclerView = findViewById<RecyclerView>(R.id.detailRecyclerViewStaticDetail)

        val layoutManager = LinearLayoutManager(this)
        detailRecyclerView.layoutManager = layoutManager

        detailAdapter = DetailsRecycleViewAdapter(model.details)
        detailRecyclerView.adapter = detailAdapter
    }

    override fun afterDatabaseQuery(data: MutableList<Float>) {
        val values = ArrayList<Entry>()
        val lineDataSet: LineDataSet

        for (i in 0 until data.size)
            values.add(Entry(i.toFloat(), data[i]/model.scalingFactor, null))

        if (chart.data != null && chart.data.dataSetCount > 0) {
            lineDataSet = chart.data.getDataSetByIndex(0) as LineDataSet
            lineDataSet.values = values
            lineDataSet.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()

        }
        else {
            lineDataSet = LineDataSet(values, model.name)
            lineDataSet.setDrawIcons(false)
            lineDataSet.color = Color.WHITE
            lineDataSet.valueTextColor = Color.WHITE

            lineDataSet.lineWidth = 1f
            lineDataSet.circleRadius = 3f

            lineDataSet.setDrawCircleHole(false)
            lineDataSet.setDrawCircles(false)
            lineDataSet.setDrawValues(false)

            // customize legend entry
            lineDataSet.formLineWidth = 1f
            lineDataSet.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            lineDataSet.formSize = 15f

            lineDataSet.valueTextSize = 9f

            // set the filled area
            lineDataSet.setDrawFilled(true)
            lineDataSet.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_purple)
                lineDataSet.fillDrawable = drawable
            } else {
                lineDataSet.fillColor = R.color.colorPrimary
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(lineDataSet) // add the data sets

            chart.data = LineData(dataSets)

        }
        chart.invalidate()
    }

    override fun getModelName(): String {
        return model.name
    }

    override fun getContext(): Context {
        return this
    }

}
