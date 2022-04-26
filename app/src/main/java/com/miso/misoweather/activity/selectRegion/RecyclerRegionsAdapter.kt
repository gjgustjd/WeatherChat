package com.miso.misoweather.activity.selectRegion

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.databinding.GridRegionBinding
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
@ActivityRetainedScoped
class RecyclerRegionsAdapter @Inject constructor(
    @ActivityContext val context: Context,
    private val regions: List<RegionItem>
) :
    RecyclerView.Adapter<RecyclerRegionsAdapter.Holder>() {

    private val viewHolders = arrayListOf<Holder>()
    var selectedIndex: Int = -1

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        viewHolders.add(holder)
        holder.bind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(GridRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    fun getSelectedItemShortName(): String {
        return regions[selectedIndex].shortName
    }

    inner class Holder(binding: GridRegionBinding) : RecyclerView.ViewHolder(binding.root) {
        val txt_name: TextView = binding.txtRegionName

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

        fun bind() {
            val layoutParams = itemView.layoutParams
            layoutParams.height = 100

            itemView.requestLayout()
            val data = regions[adapterPosition]
            setText(data.shortName)
            if (adapterPosition == selectedIndex) {
                applySelection(adapterPosition)
            }

            itemView.setOnClickListener {
                if (selectedIndex != -1) {
                    applyUnselection(selectedIndex)
                }
                selectedIndex = adapterPosition
                applySelection(selectedIndex)
            }
        }

        fun setText(listData: String) {
            txt_name.text = listData
        }
    }

}