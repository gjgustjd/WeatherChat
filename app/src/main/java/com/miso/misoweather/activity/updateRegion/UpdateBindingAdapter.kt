package com.miso.misoweather.activity.updateRegion

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.activity.selectRegion.RecyclerRegionsAdapter
import com.miso.misoweather.activity.selectRegion.RegionItem
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import javax.inject.Inject

object UpdateBindingAdapter {

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("bindUpdateData")
    @JvmStatic
    fun setUpdateAdapter(view: RecyclerView, activity: UpdateRegionActivity) {
        fun getRegionItems(context: Context): ArrayList<RegionItem> {
            val resource = context.resources
            val regions = resource.getStringArray(R.array.regions)
            val regions_full = resource.getStringArray(R.array.regions_full)
            val regionItems = arrayListOf<RegionItem>()
            for (i: Int in regions.indices) {
                val item = RegionItem().apply {
                    shortName = regions[i]
                    longName = regions_full[i]
                }
                regionItems.add(item)
            }
            return regionItems
        }

        val context = view.context
        val adapter = RecyclerRegionsAdapter(context, getRegionItems(context))
        val layoutManager = GridLayoutManager(context, 4)
        val regionList = context.resources.getStringArray(R.array.regions)
        adapter.selectedIndex = regionList.indexOf(activity.currentRegion)
        view.adapter = adapter
        view.layoutManager = layoutManager
        view.addItemDecoration(VerticalSpaceItemDecoration(30))
    }

}