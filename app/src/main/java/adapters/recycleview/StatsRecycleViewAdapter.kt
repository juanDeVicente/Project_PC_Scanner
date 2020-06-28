package adapters.recycleview

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projectpcscanner.R
import com.projectpcscanner.activities.ActivityStaticDetail
import com.projectpcscanner.models.StaticsModel
import com.vaibhavlakhera.circularprogressview.CircularProgressView
import java.util.*

class StatsRecycleViewAdapter(private val staticsModel: MutableList<StaticsModel>, private val date: Date): RecyclerView.Adapter<StatsRecycleViewAdapter.MyViewHolder>(), Filterable {

    private var staticsModelFiltered = staticsModel

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var staticNameTextView: TextView = view.findViewById(R.id.staticNameTextView)
        var progressView: CircularProgressView = view.findViewById(R.id.progressView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.stats_recycler_view_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return staticsModelFiltered.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = staticsModelFiltered[holder.adapterPosition]
        holder.view.setOnClickListener{
            val options = ActivityOptions.makeSceneTransitionAnimation(it.context as Activity, it.findViewById(R.id.progressView), "detail_transition_item")

            val intent = Intent(it.context, ActivityStaticDetail::class.java)
            Log.d("send_values", "${model.name} ${model.maxValue}, ${model.currentValue}")
            intent.putExtra("name", model.name)
            intent.putExtra("currentValue", model.currentValue)
            intent.putExtra("maxValue", model.maxValue)
            intent.putExtra("scalingFactor", model.scalingFactor)
            intent.putExtra("measurementUnit", model.measurementUnit)
            intent.putStringArrayListExtra("detailName", ArrayList(model.details.keys))
            intent.putStringArrayListExtra("detailValue", ArrayList(model.details.values))
            intent.putExtra("date", date.time)

            it.context.startActivity(intent, options.toBundle())
        }
        holder.staticNameTextView.text = model.name
        holder.progressView.setProgress(((100 * model.currentValue)/model.maxValue).toInt(), true)
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
    //https://stackoverflow.com/questions/38182223/recyclerview-wrong-position-set-onbindviewholder
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}