package com.miso.misoweather.activity.selectArea

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ListItemTownBinding
import com.miso.misoweather.model.dto.Region
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.M)
class RecyclerAreaAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val regions: List<Region>
) :
    RecyclerView.Adapter<RecyclerAreaAdapter.Holder>() {
    val selectedPosition = MutableLiveData(0)


    override fun getItemCount(): Int {
        return regions.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(regions[position])
    }

    fun selectItem(position: Int) {
        selectedPosition.value = position
    }

    fun getSelectedItem(): Region {
        return regions[selectedPosition.value!!]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_town, parent, false)
        return Holder(ListItemTownBinding.bind(view))
    }

    inner class Holder(val binding: ListItemTownBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(region: Region) {
            binding.region = region
            binding.position = adapterPosition
            binding.adapter=this@RecyclerAreaAdapter
            binding.lifecycleOwner=lifecycleOwner
        }
    }

}