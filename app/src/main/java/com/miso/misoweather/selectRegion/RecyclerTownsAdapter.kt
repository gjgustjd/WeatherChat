package com.miso.misoweather.selectRegion

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R

class RecyclerTownsAdapter(var context: Context, var regions: Array<String>) :
    RecyclerView.Adapter<RecyclerTownsAdapter.Holder>() {
    var selectedPosition: Int = -1

    var viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 140
        holder.itemView.requestLayout()
        val data = regions.get(position)
        holder.setText(data)

        holder.itemView.setOnClickListener {
            if (selectedPosition != -1) {
                var txt_name = viewHolders.get(selectedPosition).txt_name
                txt_name.setTextColor(Color.BLACK)
                txt_name.setTypeface(null,NORMAL)
            }
            selectedPosition = position
            var txt_name = viewHolders.get(selectedPosition).txt_name
            txt_name.setTextColor(context.resources.getColor(R.color.primaryPurple))
            txt_name.setTypeface(null, BOLD)
        }
        viewHolders.add(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_town, parent, false)
        return Holder(view)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_name = itemView.findViewById<TextView>(R.id.txt_region_name)
        var isSelected = false
        fun setText(listData: String) {
            txt_name.text = "${listData}"
        }
    }

}