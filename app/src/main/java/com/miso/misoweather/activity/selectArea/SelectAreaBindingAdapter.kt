package com.miso.misoweather.activity.selectArea

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.miso.misoweather.R
import com.miso.misoweather.model.dto.Region

object SelectAreaBindingAdapter {

    @BindingAdapter("areaName")
    @JvmStatic
    fun setAreaName(view: TextView, region: Region) {
        val name =
            if (region.smallScale.contains("선택 안 함"))
                "전체"
            else
                region.smallScale

        view.text = name
    }

    @BindingAdapter("areaLayoutParams")
    @JvmStatic
    fun setAreaLayoutParams(view: View, isSet: Boolean) {
        if (isSet) {
            val layoutParams = view.layoutParams
            layoutParams.height = 140
            view.requestLayout()
        }
    }

    @BindingAdapter("regionLayoutParams")
    @JvmStatic
    fun setRegionLayoutParams(view: View, isSet: Boolean) {
        if (isSet) {
            val layoutParams = view.layoutParams
            layoutParams.height = 100
            view.requestLayout()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @BindingAdapter("isAreaSelected")
    @JvmStatic
    fun applySelection(view: TextView, isSelected: Boolean) {
        view.apply {
            if (isSelected) {
                setTextColor(context.resources.getColor(R.color.primaryPurple, null))
                setTypeface(null, Typeface.BOLD)
            } else {
                setTextColor(Color.BLACK)
                setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @BindingAdapter("isRegionSelected")
    @JvmStatic
    fun applyRegionSelection(view: TextView, isSelected: Boolean) {
        view.apply {
            if (isSelected) {
                view.setTextColor(Color.WHITE)
            } else {
                view.setTextColor(context.resources.getColor(R.color.black, null))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @BindingAdapter("isRegionSelected")
    @JvmStatic
    fun applyRegionSelection(view: View, isSelected: Boolean) {
        view.apply {
            if (isSelected) {
                view.setBackgroundResource(R.drawable.grid_region_background_purple)
            } else {
                view.setBackgroundResource(R.drawable.grid_region_background)
            }
        }
    }
}