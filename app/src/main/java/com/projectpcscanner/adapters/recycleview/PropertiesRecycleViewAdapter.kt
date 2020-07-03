package com.projectpcscanner.adapters.recycleview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.models.PropertiesModel
import com.projectpcscanner.utils.getStringFromResourceByName
import java.util.*

class PropertiesRecycleViewAdapter(private val properties: List<PropertiesModel>): RecyclerView.Adapter<PropertiesRecycleViewAdapter.MyViewHolder>(),
    Filterable {
    private var propertiesFiltered = properties.toMutableList()

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.properties_recycler_view_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return propertiesFiltered.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val property = propertiesFiltered[position]
        val title = getStringFromResourceByName(holder.view.context, property.name) + ":"
        holder.view.findViewById<TextView>(R.id.propertiesTitle).text = title
        holder.view.findViewById<TextView>(R.id.propertiesValue).text = property.value
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val sequence = constraint.toString().toLowerCase(Locale.ROOT)
                if (sequence.isEmpty())
                    propertiesFiltered = properties.toMutableList()
                else {
                    propertiesFiltered = mutableListOf()
                    for (model in properties)
                        if (model.name.toLowerCase(Locale.ROOT).contains(sequence))
                            propertiesFiltered.add(model)
                }
                val filterResults = FilterResults()
                filterResults.values = propertiesFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                propertiesFiltered = results?.values as MutableList<PropertiesModel>
                notifyDataSetChanged()
            }

        }
    }
}