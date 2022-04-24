package com.miso.misoweather.Module

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.miso.misoweather.activity.selectRegion.RecyclerRegionsAdapter
import com.miso.misoweather.activity.selectRegion.RegionItem
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class RecyclerAdapterModule {
    @RequiresApi(Build.VERSION_CODES.M)
    @Provides
    @ActivityScoped
    fun getRegionAdapter(@ActivityContext context: Context): RecyclerRegionsAdapter {
        fun getRegionItems(): ArrayList<RegionItem> {
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
        return RecyclerRegionsAdapter(context, getRegionItems())
    }

    @Provides
    @ActivityScoped
    fun getGridLayoutManager(@ActivityContext context: Context): GridLayoutManager {
        return GridLayoutManager(context, 4)
    }

    @Provides
    @ActivityScoped
    fun getVerticalSpaceItemDecoration(): VerticalSpaceItemDecoration {
        return VerticalSpaceItemDecoration(30)
    }

}