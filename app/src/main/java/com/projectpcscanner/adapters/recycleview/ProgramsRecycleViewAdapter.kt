package com.projectpcscanner.adapters.recycleview

import android.content.Context
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.models.ProgramsModel
import com.projectpcscanner.tasks.RequestTask


class ProgramsRecycleViewAdapter(private val programsList: MutableList<ProgramsModel>, private val context: Context): RecyclerView.Adapter<ProgramsRecycleViewAdapter.MyViewHolder>(),
    RequestTask.RequestTaskListener {

    private val viewMap: MutableMap<Int, MyViewHolder> = mutableMapOf()

    inner class MyViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.programs_recycle_view_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return programsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = this.programsList[position]
        holder.view.findViewById<TextView>(R.id.textViewProgramItem).text = model.name
        holder.view.findViewById<ImageView>(R.id.imageViewProgramItem).setImageBitmap(BitmapFactory.decodeByteArray(model.icon, 0, model.icon.size))
        holder.view.setOnClickListener {
            val sharedPreferences = holder.view.context.getSharedPreferences(holder.view.context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            val address = sharedPreferences.getString("address", null)!!
            val port = sharedPreferences.getString("port", "5000")!!

            val startProgramTask = RequestTask(this)
            startProgramTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://${address}", port, "programs/${position}", position.toString())
            viewMap[position] = holder
            holder.view.findViewById<ProgressBar>(R.id.progressBarProgramItem).visibility = View.VISIBLE
        }
    }

    override fun afterRequest(rawData: String, tag: String) {
        val position = tag.toIntOrNull()
        if (position != null)
        {
            viewMap.remove(position)!!.view.findViewById<ProgressBar>(R.id.progressBarProgramItem).visibility = View.INVISIBLE
            val builder =
                AlertDialog.Builder(context)
            builder.setMessage(context.getString(R.string.program_dialog_success_body))
                .setTitle(context.getString(R.string.program_dialog_sucess_header))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->

                }
            val alert = builder.create()
            alert.show()
        }
    }
}