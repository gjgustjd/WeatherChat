package com.miso.misoweather.activity.weatherdetail

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.model.dto.forecast.hourly.HourlyForecast

object ForecastOnTimeBindingAdapter {
    @BindingAdapter("forecastOnTimedegree")
    @JvmStatic
    fun setForecastOnDaydegree(view: TextView, forecast: HourlyForecast) {
        view.text = CommonUtil.toIntString(forecast.temperature) + "˚"
    }

    @BindingAdapter("forecastOnTimemoji")
    @JvmStatic
    fun setForecastOnDayemoji(view: TextView, forecast: HourlyForecast) {
        view.text = forecast.weather
    }

    @BindingAdapter("forecastOnTimetime")
    @JvmStatic
    fun setForecastOnDaytime(view: TextView, forecast: HourlyForecast) {
        view.text = forecast.forecastTime.split("T")[1]
            .split(":")[0] + "시"
    }
}