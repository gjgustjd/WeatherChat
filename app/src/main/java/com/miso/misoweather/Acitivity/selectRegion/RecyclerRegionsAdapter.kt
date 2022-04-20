package com.miso.misoweather.Acitivity.selectRegion

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R

@RequiresApi(Build.VERSION_CODES.M)
class RecyclerRegionsAdapter(
    private val context: Context,
    private val regions: ArrayList<RegionItem>
) :
    RecyclerView.Adapter<RecyclerRegionsAdapter.Holder>() {
    constructor(context: Context, regions: ArrayList<RegionItem>, index: Int) : this(
        context,
        regions
    ) {
        selectedIndex = index
    }

    private val viewHolders: ArrayList<Holder> = ArrayList()
    var selectedIndex: Int = -1

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.apply {
            val layoutParams = itemView.layoutParams
            layoutParams.height = 100

            itemView.requestLayout()
            val data = regions.get(position)
            setText(data.shortName)
            viewHolders.add(holder)
            if (position == selectedIndex) {
                applySelection(position)
            }

            itemView.setOnClickListener {
                if (selectedIndex != -1) {
                    applyUnselection(selectedIndex)
                }
                selectedIndex = position
                applySelection(selectedIndex)
            }
        }
    }

    private fun applySelection(position: Int) {
        val holder = viewHolders[position]
        holder.apply {
            itemView.setBackgroundResource(R.drawable.grid_region_background_purple)
            txt_name.setTextColor(Color.WHITE)
        }
    }

    private fun applyUnselection(position: Int) {
        viewHolders[position].apply {
            itemView.setBackgroundResource(R.drawable.grid_region_background)
            txt_name.setTextColor(context.resources.getColor(R.color.black, null))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_region, parent, false)
        return Holder(view)
    }

    fun getSelectedItemShortName(): String {
        return regions[selectedIndex].shortName
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_name: TextView = itemView.findViewById(R.id.txt_region_name)
        fun setText(listData: String) {
            txt_name.text = listData
        }
    }

}