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
    constructor(context: Context, regions: ArrayList<RegionItem>, index:Int):this(context,regions)
    {
       selectedIndex = index
    }

    var viewHolders: ArrayList<Holder> = ArrayList()
    var selectedIndex: Int = -1

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 100
        holder.itemView.requestLayout()
        val data = regions.get(position)
        holder.setText(data.shortName)
        viewHolders.add(holder)
        if (position == selectedIndex) {
            applySelection(position)
        }

        holder.itemView.setOnClickListener {
            if (selectedIndex != -1) {
                applyUnselection(selectedIndex)
            }
            selectedIndex = position
            applySelection(selectedIndex)
        }
    }

    fun applySelection(position: Int) {
        var holder = viewHolders.get(position)
        holder.itemView.setBackgroundResource(R.drawable.grid_region_background_purple)
        holder.txt_name.setTextColor(Color.WHITE)
    }
    fun applyUnselection(position: Int) {
        var viewHolder = viewHolders.get(position)
        viewHolder.itemView.setBackgroundResource(R.drawable.grid_region_background)
        viewHolder.txt_name.setTextColor(context.resources.getColor(R.color.black))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_region, parent, false)
        return Holder(view)
    }

    fun getSelectedItemShortName(): String {
        return regions.get(selectedIndex).shortName
    }

    fun getSelectedItemLongName(): String {
        return regions.get(selectedIndex).longName
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_name = itemView.findViewById<TextView>(R.id.txt_region_name)
        fun setText(listData: String) {
            txt_name.text = "${listData}"
        }
    }

}