package com.projectpcscanner.activities

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.projectpcscanner.R
import com.projectpcscanner.database.entities.StaticsDataEntity
import com.projectpcscanner.database.entities.StaticsValueEntity
import com.projectpcscanner.tasks.DatabaseGetAllValuesTask
import com.projectpcscanner.tasks.DatabaseGetDataTask
import com.projectpcscanner.utils.setActivityFullScreen
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivityData : AppCompatActivity(), DatabaseGetAllValuesTask.Listener, DatabaseGetDataTask.Listener {

    var currentIndex: Int = 0
    var data: MutableMap<String, MutableList<StaticsValueEntity>> = mutableMapOf()
    private lateinit var chart: LineChart
    private lateinit var entity: StaticsDataEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_data)

        val toolbar: Toolbar = findViewById(R.id.dataToolbar)
        toolbar.title = getString(R.string.nd_data)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        chart = findViewById(R.id.chartData)
        chart.setDrawGridBackground(false)

        chart.isDragEnabled = true
        chart.setScaleEnabled(false)

        chart.setDrawBorders(true)
        chart.setBorderColor(Color.WHITE)

        chart.description.isEnabled = false
        chart.isClickable = true

        chart.legend.isEnabled = false
        chart.isHighlightPerDragEnabled = false
        chart.isHighlightPerTapEnabled = false

        chart.animateX(1000)

        findViewById<FloatingActionButton>(R.id.buttonDataRight).setOnClickListener {
            currentIndex = ++currentIndex%data.keys.size
            val databaseGetDataTask = DatabaseGetDataTask(this)
            databaseGetDataTask.execute(this.data.keys.toList()[currentIndex])
        }
        findViewById<FloatingActionButton>(R.id.buttonDataLeft).setOnClickListener {
            if (--currentIndex < 0)
                currentIndex = data.keys.size - 1
            val databaseGetDataTask = DatabaseGetDataTask(this)
            val key = this.data.keys.toList()[currentIndex]
            databaseGetDataTask.execute(key)
            findViewById<TextView>(R.id.dataTextView).text = key
        }

        val databaseGetAllValuesTask = DatabaseGetAllValuesTask(this)
        databaseGetAllValuesTask.execute()
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

    override fun getContext(): Context {
        return this
    }

    override fun afterDatabaseQuery(data: StaticsDataEntity) {
        entity = data
        findViewById<TextView>(R.id.dataTextView).text = data.name
        updateChart(this.data.keys.toList()[currentIndex])
    }

    override fun afterDatabaseQuery(list: List<StaticsValueEntity>) {
        list.forEach {
            if (!data.containsKey(it.staticsName))
                data[it.staticsName] = mutableListOf()
            data[it.staticsName]!!.add(it)
        }
        val databaseGetDataTask = DatabaseGetDataTask(this)
        databaseGetDataTask.execute(this.data.keys.toList()[currentIndex])
    }
    private fun updateChart(key: String)
    {
        val values = ArrayList<Entry>()
        val lineDataSet: LineDataSet

        for (i in 0 until data[key]!!.size)
            values.add(Entry(i.toFloat(), data[key]!![i].currentValue/entity.scalingFactor!!, null))

        if (chart.data != null && chart.data.dataSetCount > 0) {
            lineDataSet = chart.data.getDataSetByIndex(0) as LineDataSet
            lineDataSet.values = values
            lineDataSet.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()

        }
        else {
            lineDataSet = LineDataSet(values, entity.name)
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
        var xAxis: XAxis
        run {
            xAxis = chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 51f
            xAxis.valueFormatter = object : ValueFormatter() {
                private val mFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                override fun getFormattedValue(value: Float): String {
                    val millis = data[data.keys.toList()[currentIndex]]!![value.toInt()].date!!.time
                    return mFormat.format(Date(millis))
                }
            }
            xAxis.textColor = Color.WHITE
        }

        var yAxis: YAxis
        run {
            yAxis = chart.axisLeft
            chart.axisRight.isEnabled = false
            yAxis.axisMaximum = entity.maxValue!!/entity.scalingFactor!!
            yAxis.axisMinimum = entity.minValue!!
            yAxis.textColor = Color.WHITE
        }

        lineDataSet.notifyDataSetChanged()
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(50f)
        chart.animateX(1000)
        chart.invalidate()
    }
}
