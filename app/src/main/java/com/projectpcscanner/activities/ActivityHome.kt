package com.projectpcscanner.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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
import com.projectpcscanner.adapters.recycleview.StatsRecycleViewAdapter
import com.projectpcscanner.tasks.*
import com.projectpcscanner.utils.createNotification
import com.projectpcscanner.utils.exitApplication
import com.projectpcscanner.utils.openWebNavigator
import com.projectpcscanner.utils.setActivityFullScreen
import org.json.JSONArray
import java.util.*


class ActivityHome : AppCompatActivity(), RequestTask.RequestTaskListener, NavigationView.OnNavigationItemSelectedListener, DatabaseDeleteTask.Listener,
    BroadcastTask.BroadcastTaskListener {
    private var staticsTag = "statics"
    private var rebootTag = "reboot"
    private var shutdownTag = "shutdown"

    private val map: MutableMap<String, StaticsModel> = mutableMapOf()
    private var visitedMap: MutableMap<String, Boolean> = mutableMapOf()
    private var data: MutableList<StaticsModel> = mutableListOf()

    private lateinit var adapter: StatsRecycleViewAdapter

    private val handler = Handler()
    private val delay = 1200 //milliseconds

    private var firstRequest: Boolean = true
    private var deleteDatabase = false
    private var errorRequest = false

    private var allowNotification = false

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

        findViewById<Button>(R.id.buttonReconnect).setOnClickListener {
            findViewById<Button>(R.id.buttonReconnect).isEnabled = false
            findViewById<ProgressBar>(R.id.progressBarReconnect).visibility = View.VISIBLE
            BroadcastTask(this).execute()
        }
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
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                return true
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_database -> {
                val builder = AlertDialog.Builder(this)
                builder.apply {
                    setPositiveButton(R.string.delete_database) { _, _ ->
                        this@ActivityHome.startDeleteDatabaseTask()
                    }
                    setNegativeButton(R.string.close) { _, _ ->

                    }
                }

                builder.setMessage(R.string.delete_database_message)
                builder.setTitle(R.string.delete_database)

                builder.create()
                builder.show()
            }
            R.id.reset_application -> {
                val builder = AlertDialog.Builder(this)
                builder.apply {
                    setPositiveButton(R.string.reset_application) { _, _ ->
                        this@ActivityHome.startDeleteDatabaseTask()
                        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            remove("address")
                            remove("port")
                            apply()
                        }
                        val intent = Intent(this@ActivityHome, ActivityWelcome::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    }
                    setNegativeButton(R.string.close) { _, _ ->

                    }
                }

                builder.setMessage(R.string.reset_application_message)
                builder.setTitle(R.string.reset_application)

                builder.create()
                builder.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startDeleteDatabaseTask() {
        deleteDatabase = true
        val task = DatabaseDeleteTask(this)
        task.execute()
    }

    override fun afterDeleteDatabase() {
        deleteDatabase = false
        firstRequest = true
    }

    override fun getContext(): Context {
        return this
    }

    override fun afterRequest(rawData: String, tag: String) {
        if (tag == staticsTag) {
            if (errorRequest)
            {
                errorRequest = false
                findViewById<Toolbar>(R.id.toolbar).visibility = View.VISIBLE
                findViewById<RecyclerView>(R.id.staticsRecyclerView).visibility = View.VISIBLE
                findViewById<TextView>(R.id.textViewReconnect).visibility = View.GONE
                findViewById<ProgressBar>(R.id.progressBarReconnect).visibility = View.GONE
                val button = findViewById<Button>(R.id.buttonReconnect)
                button.isEnabled = true
                button.visibility = View.GONE
            }
            val jsonObject = JSONArray(rawData)

            if (!deleteDatabase) {
                if (firstRequest) {
                    firstRequest = false
                    val databaseDataTask = DatabaseDataTask(this)
                    databaseDataTask.execute(jsonObject)
                }

                val databaseValueTask = DatabaseValueTask(this)
                databaseValueTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject)
            }

            for (i in 0 until jsonObject.length()) {
                val model = StaticsModel(jsonObject.getJSONObject(i))
                if (map.containsKey(model.name)) {
                    map[model.name]!!.currentValue = model.currentValue
                    for (j in 0 until data.size) {
                        if (data[j].name == model.name) {
                            data[j] = model
                        }
                    }
                }
                else {
                    if (allowNotification)
                        createNotification(this, getString(R.string.device_connected_title), getString(R.string.device_connected, model.name))
                    map[model.name] = model
                    data.add(model)
                }
                visitedMap[model.name] = true
            }
            for(i in visitedMap) {
                if (!i.value) {
                    createNotification(this, getString(R.string.device_disconnected_title), getString(R.string.device_disconnected, i.key))
                    data.remove(map.remove(i.key))
                }
            }
            visitedMap = visitedMap.filterValues {
                it
            }.toMutableMap()
            for (k in visitedMap.keys)
                visitedMap[k] = false

            adapter.notifyDataSetChanged()
            allowNotification = true
        }
    }

    override fun requestError() {
        if (!errorRequest)
        {
            errorRequest = true //Ha ocurrido un error
            findViewById<Toolbar>(R.id.toolbar).visibility = View.INVISIBLE
            findViewById<RecyclerView>(R.id.staticsRecyclerView).visibility = View.GONE
            findViewById<TextView>(R.id.textViewReconnect).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.progressBarReconnect).visibility = View.INVISIBLE
            findViewById<Button>(R.id.buttonReconnect).visibility = View.VISIBLE
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
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return
        val address = sharedPreferences.getString("address", null)
        val port = sharedPreferences.getString("port", "5000")
        handler.postDelayed(object : Runnable {
            override fun run() {
                val staticsRequestTask = RequestTask(this@ActivityHome)
                staticsRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", port, "statics", staticsTag)
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
            R.id.navigation_tasks -> {
                val intent = Intent(this, ActivityTasks::class.java)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            R.id.navigation_programs -> {
                val intent = Intent(this, ActivityPrograms::class.java)
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
            R.id.navigation_reboot -> {
                val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return false
                val address = sharedPreferences.getString("address", null)
                val port = sharedPreferences.getString("port", "5000")
                val rebootTask = RequestTask(this)
                rebootTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", port, "reboot", rebootTag)
            }
            R.id.navigation_shutdown -> {
                val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return false
                val address = sharedPreferences.getString("address", null)
                val port = sharedPreferences.getString("port", "5000")
                val rebootTask = RequestTask(this)
                rebootTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", port, "shutdown", shutdownTag)
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

    override fun afterBroadcast(address: String, port: String) {
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)?: return
        with(sharedPreferences.edit()) {
            putString("address", address)
            putString("port", port)
            apply()
        }
        handler.removeCallbacksAndMessages(null)
        startRequestStatics()
    }

    override fun errorBroadcast() {
        findViewById<Button>(R.id.buttonReconnect).isEnabled = true
        findViewById<ProgressBar>(R.id.progressBarReconnect).visibility = View.INVISIBLE
    }
}
