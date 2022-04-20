package com.miso.misoweather.Acitivity.selectTown

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.model.DTO.Region
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.M)
class RecyclerTownsAdapter(
    private val context: Context,
    private val regions: List<Region>
) :
    RecyclerView.Adapter<RecyclerTownsAdapter.Holder>() {
    var selectedPosition: Int = 0

    private val viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.apply {
            val layoutParams = itemView.layoutParams
            layoutParams.height = 140
            itemView.requestLayout()
            regions[position].apply {
                var name = midScale
                if (midScale.contains("선택 안 함"))
                    name = "전체"

                setText(name)
            }
            applySelection(this@apply, selectedPosition == position)
            itemView.setOnClickListener {
                selectItem(position)
            }
            viewHolders.add(this@apply)
        }
    }

    private fun applySelection(holder: Holder, isSelected: Boolean) {
        try {
            val txt_name = holder.txt_name
            txt_name.apply {
                if (isSelected) {
                    setTextColor(context.resources.getColor(R.color.primaryPurple, null))
                    setTypeface(null, BOLD)
                } else {
                    setTextColor(Color.BLACK)
                    setTypeface(null, NORMAL)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun selectItem(position: Int) {
        try {
            if (selectedPosition != -1) {
                applySelection(viewHolders[selectedPosition], false)
            }
            selectedPosition = position
            applySelection(viewHolders[selectedPosition], true)
        } catch (e: Exception) {
            e.printStackTrace()
            selectedPosition = position
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_town, parent, false)
        return Holder(view)
    }

    fun getSelectedItem(): Region {
        return regions[selectedPosition]
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_name = itemView.findViewById<TextView>(R.id.txt_region_name)
        fun setText(listData: String) {
            txt_name.text = listData
        }
    }

}