package com.miso.misoweather

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class GridRegionsAdapter(var context: Context, var regions:ArrayList<String>):BaseAdapter(){
    override fun getCount(): Int {
        return regions.size
    }

    override fun getItem(position: Int): Any {
        return regions.get(position)
    }

    override fun getItemId(position: Int): Long {
        return regions.get(position).toLong()
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

    }
}