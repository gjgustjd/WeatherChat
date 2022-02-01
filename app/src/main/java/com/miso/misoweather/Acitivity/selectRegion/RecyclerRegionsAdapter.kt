package com.miso.misoweather.Acitivity.selectRegion

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R

class RecyclerRegionsAdapter(var context: Context, var regions: ArrayList<RegionItem>) :
    RecyclerView.Adapter<RecyclerRegionsAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()
    var selectedIndex: Int = -1

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 80
        holder.itemView.requestLayout()
        val data = regions.get(position)
        holder.setText(data.shortName)
        if(position==0) {
            selectedIndex = position
            holder.itemView.setBackgroundResource(R.drawable.grid_region_background_purple)
            holder.txt_name.setTextColor(Color.WHITE)
        }

        holder.itemView.setOnClickListener {
            if (selectedIndex != -1) {
                var viewHolder = viewHolders.get(selectedIndex)
                viewHolder.itemView.setBackgroundResource(R.drawable.grid_region_background)
                viewHolder.txt_name.setTextColor(context.resources.getColor(R.color.primaryPurple))
            }
            selectedIndex = position
            holder.itemView.setBackgroundResource(R.drawable.grid_region_background_purple)
            holder.txt_name.setTextColor(Color.WHITE)

        }
        viewHolders.add(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_region, parent, false)
        return Holder(view)
    }

    fun getSelectedItemShortName():String
    {
        return regions.get(selectedIndex).shortName
    }

    fun getSelectedItemLongName():String
    {
        return regions.get(selectedIndex).longName
    }
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_name = itemView.findViewById<TextView>(R.id.txt_region_name)
        fun setText(listData: String) {
            txt_name.text = "${listData}"
        }
    }

}