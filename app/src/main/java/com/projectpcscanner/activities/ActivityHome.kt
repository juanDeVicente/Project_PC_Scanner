package com.projectpcscanner.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.projectpcscanner.R
import com.projectpcscanner.models.StaticsModel
import com.projectpcscanner.recycleviewadapters.StatsRecycleViewAdapter
import com.projectpcscanner.tasks.DatabaseDataTask
import com.projectpcscanner.tasks.DatabaseValueTask
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.exitApplication
import com.projectpcscanner.utils.openWebNavigator
import com.projectpcscanner.utils.setActivityFullScreen
import org.json.JSONObject
import java.util.*


class ActivityHome : AppCompatActivity(), RequestTask.RequestTaskListener, NavigationView.OnNavigationItemSelectedListener {
    private var staticsTag = "statics"

    private lateinit var map: MutableMap<String, StaticsModel>
    private var data: MutableList<StaticsModel> = mutableListOf()

    private lateinit var adapter: StatsRecycleViewAdapter

    private val handler = Handler()
    private val delay = 1200 //milliseconds

    private var firstRequest: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "PC Scanner"
        toolbar.navigationIcon = getDrawable(R.drawable.baseline_menu_24)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            0,
            0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.navigation_drawer_view)
        navigationView.setNavigationItemSelectedListener(this)

        map = mutableMapOf()
        val recyclerView = findViewById<RecyclerView>(R.id.staticsRecyclerView)

        val layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        else
            GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = layoutManager

        data = map.values.toMutableList()
        val currentDate = Date()
        adapter = StatsRecycleViewAdapter(data, currentDate)
        recyclerView.adapter = adapter
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            showExitDialog().show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        val searchItem = menu?.findItem(R.id.search)
        searchItem!!.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                return true;
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                return true;
            }

        })
        val searchView = searchItem.actionView as SearchView
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
            val jsonObject = JSONObject(rawData).getJSONArray("elements")

            if (firstRequest){
                firstRequest = false
                val databaseDataTask = DatabaseDataTask(this)
                databaseDataTask.execute(jsonObject)
            }

            val databaseValueTask = DatabaseValueTask(this)
            databaseValueTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject)

            for (i in 0 until jsonObject.length()) {
                val model = StaticsModel(jsonObject.getJSONObject(i))
                map[model.name] = model
            }
            for (j in map.values.indices)
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
                handler.postDelayed(this, delay.toLong())
            }
        }, 0) //0 por que al principio quiero que se mande una petición para obtener los datos
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_properties -> {
                val intent = Intent(this, ActivityProperties::class.java)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            R.id.navigation_data -> {
                val intent = Intent(this, ActivityData::class.java)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            R.id.navigation_contact -> {
                intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "juandvtortosa@gmail.com", null))
                startActivity(Intent.createChooser(intent, "Enviar email..."))
            }
            R.id.navigation_help -> {
                openWebNavigator(this, "https://github.com/juanDeVicente/Project_PC_Scanner/wiki")
            }
            R.id.navigation_repo -> {
                openWebNavigator(this, "https://github.com/juanDeVicente/Project_PC_Scanner")
            }
            R.id.navigation_close -> {
                showExitDialog().show()
                return true
            }
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showExitDialog(): AlertDialog {
        return this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton(R.string.exit) { _, _ ->
                    exitApplication(this@ActivityHome)
                }
                setNegativeButton(R.string.close) { _, _ ->
                    // User cancelled the dialog
                }
            }
            // Set other dialog properties
            builder.setMessage(R.string.exit_message)
            builder.setTitle(R.string.exit)
            // Create the AlertDialog
            builder.create()
        }
    }
}
