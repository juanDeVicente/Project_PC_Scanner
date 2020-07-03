package com.projectpcscanner.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Configuration
import android.media.Image
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.adapters.recycleview.TasksRecycleViewAdapter
import com.projectpcscanner.models.TasksModel
import com.projectpcscanner.tasks.RequestTask
import com.projectpcscanner.utils.setActivityFullScreen
import org.json.JSONObject


class ActivityTasks : AppCompatActivity(), RequestTask.RequestTaskListener {
    private val tasksTag = "tasks"
    private val deleteTag = "delete_task"

    private var listApplication: MutableList<TasksModel> = mutableListOf()
    private val adapterApplications = TasksRecycleViewAdapter(listApplication)
    private var isApplicationOpen = false

    private var listBackground: MutableList<TasksModel> = mutableListOf()
    private val adapterBackground = TasksRecycleViewAdapter(listBackground)
    private var isBackgroundOpen = false

    private lateinit var applicationsText: String
    private lateinit var backgroundTaskText: String

    private var running: Boolean = false
    private var sendTaskRequest = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullScreen(this)
        setContentView(R.layout.activity_tasks)

        val toolbar: Toolbar = findViewById(R.id.tasksToolbar)
        toolbar.title = getString(R.string.nd_tasks)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val cl: ConstraintLayout = findViewById(R.id.constraintLayoutTask)
        val cs = ConstraintSet()
        cs.clone(cl)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            cs.setVerticalBias(R.id.dividerHorizontalTask, 0.1f)
        else
            cs.setVerticalBias(R.id.dividerHorizontalTask, 0.18f)
        cs.applyTo(cl)

        applicationsText = getString(R.string.applications)
        backgroundTaskText = getString(R.string.background_task)

        initRequestTask()
        val buttonAnimationListener = View.OnClickListener {

            val rotate =
                if (isApplicationOpen)
                    RotateAnimation(-180f, 0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f)
                else
                    RotateAnimation(0f, -180f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f)
            rotate.duration = 250
            rotate.repeatCount = 0
            rotate.fillAfter = true
            findViewById<ImageView>(R.id.imageViewTaskApplication).startAnimation(rotate)
            isApplicationOpen = !isApplicationOpen

            if (isApplicationOpen)
            {
                val recycleView = findViewById<RecyclerView>(R.id.recyclerViewTaskApplication)
                recycleView.visibility = View.VISIBLE
                recycleView.alpha = 0.0f
                recycleView.animate()
                    .alpha(1f)
                    .setListener(null)
            }
            else
            {
                val recycleView = findViewById<RecyclerView>(R.id.recyclerViewTaskApplication)
                recycleView.alpha = 1f
                recycleView.animate()
                    .alpha(0f)
                    .setListener(object: AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            recycleView.visibility = View.GONE
                        }
                    })
            }
        }

        findViewById<ImageView>(R.id.imageViewTaskApplication).setOnClickListener(buttonAnimationListener)
        findViewById<Button>(R.id.buttonTasksApplication).setOnClickListener(buttonAnimationListener)

        val recycleViewApplication = findViewById<RecyclerView>(R.id.recyclerViewTaskApplication)
        recycleViewApplication.layoutManager = LinearLayoutManager(this)
        recycleViewApplication.adapter = adapterApplications

        val recycleViewBackground = findViewById<RecyclerView>(R.id.recyclerViewTaskBackground)
        recycleViewBackground.layoutManager = LinearLayoutManager(this)
        recycleViewBackground.adapter = adapterBackground

        val buttonBackgroundListener = View.OnClickListener {

            val rotate =
                if (isBackgroundOpen)
                    RotateAnimation(-180f, 0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f)
                else
                    RotateAnimation(0f, -180f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f)
            rotate.duration = 250
            rotate.repeatCount = 0
            rotate.fillAfter = true
            findViewById<ImageView>(R.id.imageViewTaskBackground).startAnimation(rotate)
            isBackgroundOpen = !isBackgroundOpen

            if (isBackgroundOpen)
            {
                val recycleView = findViewById<RecyclerView>(R.id.recyclerViewTaskBackground)
                recycleView.visibility = View.VISIBLE
                recycleView.alpha = 0.0f
                recycleView.animate()
                    .alpha(1f)
                    .setListener(null)
            }
            else
            {
                val recycleView = findViewById<RecyclerView>(R.id.recyclerViewTaskBackground)
                recycleView.alpha = 1f
                recycleView.animate()
                    .alpha(0f)
                    .setListener(object: AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            recycleView.visibility = View.GONE
                        }
                    })
            }
        }

        findViewById<ImageView>(R.id.imageViewTaskBackground).setOnClickListener(buttonBackgroundListener)
        findViewById<Button>(R.id.buttonTasksBackground).setOnClickListener(buttonBackgroundListener)

        val simpleItemTouchCallbackApplication = object :ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val item = listApplication.removeAt(pos)
                adapterApplications.notifyDataSetChanged()
                sendTaskRequest = false
                sendDeleteTask(item.PID)
            }
        }
        val itemTouchHelperApplication = ItemTouchHelper(simpleItemTouchCallbackApplication)
        itemTouchHelperApplication.attachToRecyclerView(recycleViewApplication)

        val simpleItemTouchCallbackBackground = object :ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val item = listBackground.removeAt(pos)
                adapterBackground.notifyDataSetChanged()
                sendTaskRequest = false
                sendDeleteTask(item.PID)
            }
        }
        val itemTouchHelperBackground = ItemTouchHelper(simpleItemTouchCallbackBackground)
        itemTouchHelperBackground.attachToRecyclerView(recycleViewBackground)

        running = true

    }

    override fun finish() {
        super.finish()
        running = false
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }

    override fun afterRequest(rawData: String, tag: String) {
        if (tag  == tasksTag && sendTaskRequest) {
            val obj = JSONObject(rawData)
            var array = obj.getJSONArray("applications")

            for (i in 0 until array.length()) {
                val task = TasksModel(array.getJSONObject(i))
                val index = listApplication.indexOf(task)
                if (index == -1)
                    listApplication.add(task)
                else
                    listApplication[index] = task
            }

            if (!sendTaskRequest)
                return

            adapterApplications.notifyDataSetChanged()

            array = obj.getJSONArray("process")

            for (i in 0 until array.length()) {
                val task = TasksModel(array.getJSONObject(i))
                val index = listBackground.indexOf(task)
                if (index == -1)
                    listBackground.add(task)
                else
                    listBackground[index] = task
            }

            if (!sendTaskRequest)
                return

            adapterBackground.notifyDataSetChanged()

            if (this.running) {
                initRequestTask()
            }
        }
        else if (tag == deleteTag)
        {
            sendTaskRequest = true
            initRequestTask()

        }

    }

    override fun requestError() {
        this.finish()
    }

    private fun initRequestTask() {
        val sharedPreferences =
            getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
        val address = sharedPreferences.getString("address", null)
        val port = sharedPreferences.getString("port", "5000")

        val taskRequestTask = RequestTask(this)
        taskRequestTask.executeOnExecutor(
            AsyncTask.THREAD_POOL_EXECUTOR,
            "http://${address}",
            port,
            "tasks",
            tasksTag
        )
    }

    private fun sendDeleteTask(PID: Int) {
        val sharedPreferences =
            getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
        val address = sharedPreferences.getString("address", null)
        val port = sharedPreferences.getString("port", "5000")

        val taskRequestTask = RequestTask(this)
        taskRequestTask.executeOnExecutor(
            AsyncTask.THREAD_POOL_EXECUTOR,
            "http://${address}",
            port,
            "tasks/${PID}",
            deleteTag
        )
    }
}
