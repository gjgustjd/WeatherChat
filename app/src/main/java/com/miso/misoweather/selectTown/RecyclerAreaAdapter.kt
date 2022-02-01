package com.miso.misoweather.selectTown

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.model.DTO.Region
import java.lang.Exception

class RecyclerAreaAdapter(var context: Context, var regions: List<Region>) :
    RecyclerView.Adapter<RecyclerAreaAdapter.Holder>() {
    var selectedPosition: Int = 0

    var viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 140
        holder.itemView.requestLayout()
        var region = regions.get(position)
        var name: String = region.smallScale
        if (region.smallScale.contains("선택 안 함"))
            name = "전체"
        holder.setText(name)
        applySelection(holder,selectedPosition==position)
        holder.itemView.setOnClickListener {
            selectItem(position)
        }
        viewHolders.add(holder)
    }

    fun applySelection(holder: Holder, isSelected:Boolean)
    {
        try {
            var txt_name = holder.txt_name
            if (isSelected) {
                txt_name.setTextColor(context.resources.getColor(R.color.primaryPurple))
                txt_name.setTypeface(null, BOLD)
            } else {
                txt_name.setTextColor(Color.BLACK)
                txt_name.setTypeface(null, NORMAL)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

     fun selectItem(position:Int)
    {
        try {
            if (selectedPosition != -1) {
                applySelection(viewHolders.get(selectedPosition), false)
            }
            selectedPosition = position
            applySelection(viewHolders.get(selectedPosition), true)
        }catch (e:Exception)
        {
            selectedPosition = position
           e.printStackTrace()
        }
    }

    fun getSelectedItem(): Region {
        return regions.get(selectedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_town, parent, false)
        return Holder(view)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_name = itemView.findViewById<TextView>(R.id.txt_region_name)
        fun setText(listData: String) {
            txt_name.text = "${listData}"
        }
    }

}