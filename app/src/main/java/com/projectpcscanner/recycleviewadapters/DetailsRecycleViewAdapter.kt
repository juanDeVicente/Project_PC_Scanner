package com.projectpcscanner.recycleviewadapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.utils.getStringFromResourceByName
import java.lang.Exception


class DetailsRecycleViewAdapter(private val details: Map<String, String>) : RecyclerView.Adapter<DetailsRecycleViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.detail_recycler_view_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return details.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val text = getStringFromResourceByName(holder.view.context, ArrayList(details.keys)[position]) + ":"
        holder.view.findViewById<TextView>(R.id.detailNameTextView).text = text

        holder.view.findViewById<TextView>(R.id.detailValueTextView).text = getStringFromResourceByName(holder.view.context, ArrayList(details.values)[position])

    }
}