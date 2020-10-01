package com.projectpcscanner.activities

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.adapters.recycleview.ProgramsRecycleViewAdapter
import com.projectpcscanner.models.ProgramsModel
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.setActivityFullScreen
import org.json.JSONArray

class ActivityPrograms : AppCompatActivity(), RequestTask.RequestTaskListener {

    private val programsTag = "programs"

    private var handler: Handler = Handler()
    private lateinit var address: String
    private lateinit var port: String

    private val programs: MutableList<ProgramsModel> = mutableListOf()
    private lateinit var adapter: ProgramsRecycleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_programs)

        val toolbar: Toolbar = findViewById(R.id.programsToolbar)
        toolbar.title = getString(R.string.nd_programs)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return
        address = sharedPreferences.getString("address", null)!!
        port = sharedPreferences.getString("port", "5000")!!

        handler.postDelayed(object: Runnable{
            override fun run() {
                val programsTask = RequestTask(this@ActivityPrograms)
                programsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", port, "programs", programsTag)
                handler.postDelayed(this, 5000)
            }

        },0)

        adapter = ProgramsRecycleViewAdapter(programs, this)

        val recyclerView = findViewById<RecyclerView>(R.id.programsRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun finish() {
        super.finish()
        handler.removeCallbacksAndMessages(null) //Quitamos el handler
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    override fun afterRequest(rawData: String, tag: String) {
        if (tag == programsTag) {
            val jsonArray = JSONArray(rawData)

            for (i in 0 until jsonArray.length())
            {
                val program = ProgramsModel(jsonArray.getJSONObject(i))
                if (i >= programs.size)
                    programs.add(program)
                else
                    programs[i] = program
            }

            adapter.notifyDataSetChanged()
        }

    }
}
