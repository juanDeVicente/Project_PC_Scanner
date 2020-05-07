package com.projectpcscanner.activities

import android.content.Context
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.models.StaticsModel
import com.projectpcscanner.recycleviewadapters.StatsRecycleViewAdapter
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.exitApplication
import com.projectpcscanner.utils.setActivityFullScreen
import org.json.JSONObject


class ActivityHome : AppCompatActivity(), RequestTask.RequestTaskListener {
    private var staticsTag = "statics"

    private lateinit var map: MutableMap<String, StaticsModel>
    private var data: MutableList<StaticsModel> = mutableListOf()

    private lateinit var adapter: StatsRecycleViewAdapter

    private val handler = Handler()
    private val delay = 1000 //milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "PC Scanner"
        toolbar.navigationIcon = getDrawable(R.drawable.baseline_menu_24)
        setSupportActionBar(toolbar)

        map = mutableMapOf()
        val recyclerView = findViewById<RecyclerView>(R.id.staticsRecyclerView)

        val layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        else
            GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = layoutManager

        data = map.values.toMutableList()
        adapter = StatsRecycleViewAdapter(data)
        recyclerView.adapter = adapter
    }

    override fun onBackPressed() {
        exitApplication(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Introduce un filtro"
        searchView.setOnQueryTextListener(object : OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.filter.filter(query)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun afterRequest(rawData: String, tag: String) {
        if (tag == staticsTag) {
            val jsonObject = JSONObject(rawData)
            for (i in 0 until jsonObject.getJSONArray("elements").length()) {
                val model = StaticsModel(jsonObject.getJSONArray("elements").getJSONObject(i))
                map[model.name] = model
            }
            for (j  in map.values.indices)
                if (j < data.size)
                    data[j] = map.values.toList()[j]
                else
                    data.add(map.values.toList()[j])

            adapter.notifyDataSetChanged()
        }
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
                val staticsRequestTask = RequestTask(this@ActivityHome)
                staticsRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", "5000", "statics", staticsTag)
                Log.d("Handler", "Ejecuto")
                handler.postDelayed(this, delay.toLong())
            }
        }, 0) //0 por que al principio quiero que se mande una petición para obtener los datos
    }
}
