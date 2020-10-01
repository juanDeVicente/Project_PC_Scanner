package com.projectpcscanner.adapters.recycleview

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.models.TasksModel


class TasksRecycleViewAdapter(private val tasks: MutableList<TasksModel>, private val listener: Listener): RecyclerView.Adapter<TasksRecycleViewAdapter.MyViewHolder>() {

    interface Listener {
        fun onItemPressed(adapter: TasksRecycleViewAdapter)
        fun onItemReleased(adapter: TasksRecycleViewAdapter)
    }

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_recycle_view_item, parent, false)
        v.setOnTouchListener { _, event ->
            v.performClick()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.listener.onItemPressed(this)
                }
                MotionEvent.ACTION_UP -> {
                    this.listener.onItemReleased(this)
                }
            }
            false
        }
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = tasks[position]
        holder.view.findViewById<ImageView>(R.id.imageViewTaskIcon).setImageBitmap(BitmapFactory.decodeByteArray(model.icon, 0, model.icon.size))
        holder.view.findViewById<TextView>(R.id.textViewTaskNameGroup).text = model.name
        holder.view.findViewById<TextView>(R.id.textViewTaskDescriptionGroup).text = model.description
        holder.view.findViewById<TextView>(R.id.textViewTaskPIDGroup).text = model.PID.toString()
        holder.view.findViewById<TextView>(R.id.textViewTaskUserGroup).text = model.username
        holder.view.findViewById<TextView>(R.id.textViewTaskMemoryGroup).text = model.memoryUse
    }
}