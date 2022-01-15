package com.miso.misoweather

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class RecyclerRegionsAdapter(var context: Context, var regions:Array<String>):RecyclerView.Adapter<RecyclerRegionsAdapter.Holder>(){
    override fun getItemCount(): Int {
        return regions.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height=140
        holder.itemView.requestLayout()
        val data = regions.get(position)
       holder.setText(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_region,parent,false)
            return Holder(view)
    }

    class Holder(itemView:View):RecyclerView.ViewHolder(itemView){
        var txt_name = itemView.findViewById<TextView>(R.id.txt_region_name)
        fun setText(listData:String)
        {
            txt_name.text = "${listData}"
        }
    }

}