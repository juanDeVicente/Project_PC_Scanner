package com.projectpcscanner.recycleviewadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.models.StaticsModel
import com.vaibhavlakhera.circularprogressview.CircularProgressView
import java.util.*

class StatsRecycleViewAdapter(private val staticsModel: MutableList<StaticsModel>): RecyclerView.Adapter<StatsRecycleViewAdapter.MyViewHolder>(), Filterable {

    private var staticsModelFiltered = staticsModel

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.stats_recycler_view_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return staticsModelFiltered.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = staticsModel[staticsModel.indexOf(staticsModelFiltered[position])]
        val staticNameTextView = holder.view.findViewById<TextView>(R.id.staticNameTextView)
        val progressView = holder.view.findViewById<CircularProgressView>(R.id.progressView)

        staticNameTextView.text = model.name
        if (model.relative)
        {
            progressView.setTotal(100)
            progressView.setProgress(((100 * model.currentValue)/model.maxValue).toInt(), true)
            progressView.setProgressTextType(CircularProgressView.PROGRESS_TEXT_TYPE_PERCENT)
        }
        else
        {
            progressView.setTotal(model.maxValue.toInt())
            progressView.setProgress(model.currentValue.toInt(), true)
        }
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val sequence = constraint.toString().toLowerCase(Locale.ROOT)
                if (sequence.isEmpty())
                    staticsModelFiltered = staticsModel
                else {
                    staticsModelFiltered = mutableListOf()
                    for (model in staticsModel)
                        if (model.name.toLowerCase(Locale.ROOT).contains(sequence))
                            staticsModelFiltered.add(model)
                }
                val filterResults = FilterResults()
                filterResults.values = staticsModelFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                staticsModelFiltered = results?.values as MutableList<StaticsModel>
                notifyDataSetChanged()
            }

        }
    }
}