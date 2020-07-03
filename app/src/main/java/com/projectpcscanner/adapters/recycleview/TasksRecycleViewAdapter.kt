package com.projectpcscanner.adapters.recycleview

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.models.TasksModel

class TasksRecycleViewAdapter(private val tasks: MutableList<TasksModel>): RecyclerView.Adapter<TasksRecycleViewAdapter.MyViewHolder>() {
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_recycle_view_item, parent, false)
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