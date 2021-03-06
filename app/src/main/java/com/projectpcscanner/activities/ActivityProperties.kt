package com.projectpcscanner.activities

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.models.PropertiesModel
import com.projectpcscanner.recycleviewadapters.PropertiesRecycleViewAdapter
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.setActivityFullScreen
import org.json.JSONObject

class ActivityProperties : AppCompatActivity(), RequestTask.RequestTaskListener {

    private lateinit var adapter: PropertiesRecycleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_properties)

        val toolbar: Toolbar = findViewById(R.id.propertiesToolbar)
        toolbar.title = getString(R.string.nd_properties)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return
        val address = sharedPreferences.getString("address", null)

        val staticsRequestTask = RequestTask(this)
        staticsRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", "5000", "properties", "")
    }

    override fun afterRequest(rawData: String, tag: String) {
        val jsonObject = JSONObject(rawData).getJSONArray("elements")
        val properties = mutableListOf<PropertiesModel>()
        for (i in 0 until jsonObject.length())
            properties.add(PropertiesModel(jsonObject.getJSONObject(i).getString("name"), jsonObject.getJSONObject(i).getString("value")))

        val layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.propertiesRecyclerView).layoutManager = layoutManager

        adapter = PropertiesRecycleViewAdapter(properties.toList())
        findViewById<RecyclerView>(R.id.propertiesRecyclerView).adapter = adapter
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.properties_menu, menu)

        val searchItem = menu?.findItem(R.id.searchProperties)
        val searchView = searchItem!!.actionView as SearchView
        searchView.queryHint = "Introduce un filtro"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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
