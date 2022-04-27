package com.miso.misoweather.activity.selectRegion

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.databinding.GridRegionBinding
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
@ActivityRetainedScoped
class RecyclerRegionsAdapter @Inject constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val regions: List<RegionItem>
) :
    RecyclerView.Adapter<RecyclerRegionsAdapter.Holder>() {
    val selectedIndex = MutableLiveData(-1)

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(regions[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.grid_region, parent, false)
        return Holder(GridRegionBinding.bind(view))
    }

    fun selectItem(position: Int) {
        selectedIndex.value = position
    }

    fun getSelectedItemShortName(): String {
        return regions[selectedIndex.value!!].shortName
    }

    inner class Holder(val binding: GridRegionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(region: RegionItem) {
            binding.region = region
            binding.position = adapterPosition
            binding.adapter = this@RecyclerRegionsAdapter
            binding.lifecycleOwner = lifecycleOwner
        }
    }

}