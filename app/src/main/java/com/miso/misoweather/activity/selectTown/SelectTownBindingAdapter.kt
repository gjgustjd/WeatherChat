package com.miso.misoweather.activity.selectTown

import android.graphics.Color
import android.graphics.Typeface
import com.miso.misoweather.model.dto.Region
import android.os.Build
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.activity.selectArea.RecyclerAreaAdapter
import com.miso.misoweather.activity.selectArea.SelectAreaActivity
import com.miso.misoweather.activity.selectRegion.RecyclerRegionsAdapter
import com.miso.misoweather.activity.selectRegion.RegionItem
import com.miso.misoweather.activity.selectRegion.SelectRegionActivity
import com.miso.misoweather.common.RegionActivity
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import java.lang.Exception

object SelectTownBindingAdapter {

    @BindingAdapter("regionVisibility")
    @JvmStatic
    fun setRegionVisibility(view: View, activity: RegionActivity?) {
        activity?.let {
            if (activity is SelectRegionActivity)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.INVISIBLE
        }
    }

    @BindingAdapter("linearTownVisibility")
    @JvmStatic
    fun setTownVisibility(view: View, activity: RegionActivity?) {
        activity?.let {
            if (activity is SelectRegionActivity)
                view.visibility = View.INVISIBLE
            else
                view.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @BindingAdapter("regionActivity","recyclerData")
    @JvmStatic
    fun setRegionRecyclerData(view: RecyclerView, activity: RegionActivity?,data:List<*>?) {
        fun setRecyclerRegions() {
            fun getRegionItems(): List<RegionItem> {
                val resource = view.context.resources
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
                return regionItems.map { it }
            }

            val regionAdapter = RecyclerRegionsAdapter(view.context, getRegionItems())

            view.apply {
                adapter = regionAdapter
                layoutManager = GridLayoutManager(view.context, 4)
                addItemDecoration(VerticalSpaceItemDecoration(30))
            }
        }

        fun setRecyclerTowns() {
            try {
                val recyclerData =
                    data as List<Region>
                val recyclerAdapter = RecyclerAreaAdapter(activity!!,recyclerData)
                view.apply {
                    adapter = recyclerAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                    if (!activity!!.midScale.equals(""))
                        recyclerAdapter.selectItem(recyclerData.indexOf(recyclerData.first() {
                            it.midScale.equals(activity.midScale)
                        }))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun setRecyclerArea() {
            try {
                val recyclerData =
                    data as List<Region>
                val recyclerAdapter = RecyclerAreaAdapter(activity!!,recyclerData)
                view.apply {
                    adapter = recyclerAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            LinearLayout.VERTICAL
                        )
                    )
                }
                if (!activity!!.smallScale.equals(""))
                    recyclerAdapter.selectItem(recyclerData.indexOf(recyclerData.first {
                        it.smallScale.equals(activity.smallScale)
                    }))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        activity?.let {
            when (it) {
                is SelectRegionActivity -> {
                    setRecyclerRegions()
                }
                is SelectTownActivity -> {
                    setRecyclerTowns()
                }
                is SelectAreaActivity -> {
                    setRecyclerArea()
                }
            }
        }
    }
}