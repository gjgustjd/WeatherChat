package com.miso.misoweather.activity.weatherdetail

import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.model.dto.forecast.daily.DailyForecast
import java.time.ZoneId
import java.time.ZonedDateTime

object ForecastOnDayBindingAdapter {

    @BindingAdapter("forecastOnDaydegreeMax")
    @JvmStatic
    fun setForecastOnDaydegreeMax(view: TextView, forecast: DailyForecast) {
        view.text = CommonUtil.toIntString(forecast.maxTemperature) + "˚"
    }

    @BindingAdapter("forecastOnDaydegreeMin")
    @JvmStatic
    fun setForecastOnDaydegreeMin(view: TextView, forecast: DailyForecast) {
        view.text = forecast.minTemperature.split(".")[0] + "˚"
    }

    @BindingAdapter("forecastOnDayemoji")
    @JvmStatic
    fun setForecastOnDayemoji(view: TextView, forecast: DailyForecast) {
        view.text = forecast.weather
    }

    @BindingAdapter("forecastOnDaypopEmoji")
    @JvmStatic
    fun setForecastOnDaypopEmoji(view: TextView, forecast: DailyForecast) {
        view.text = forecast.popIcon
    }

    @BindingAdapter("forecastOnDaypopDegree")
    @JvmStatic
    fun setForecastOnDaypopDegree(view: TextView, forecast: DailyForecast) {
        view.text = forecast.pop + "%"
    }


    @BindingAdapter("forecastOnDaytime")
    @JvmStatic
    fun setForecastOnDaytime(view: TextView, forecast: DailyForecast) {
        val forecastDate = forecast.forecastTime.split("T")[0]
        view.text = forecastDate.split("-")[1] + "/" + forecastDate.split("-")[2]
    }

    @BindingAdapter("forecastOnDayday", "position")
    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun setForecastOnDayday(view: TextView, forecast: DailyForecast, position: Int) {
        val dayNameList = listOf("월", "화", "수", "목", "금", "토", "일")
        fun getDayString(position: Int): String {
            val dayPosition = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).dayOfWeek.value
            if (position == 0) {
                return "오늘"
            } else {
                val sumPosition = dayPosition + position
                return if (sumPosition > 7) {
                    val index = (sumPosition % 7) - 1
                    dayNameList[index]
                } else
                    dayNameList[sumPosition - 1]
            }
        }
        view.text = getDayString(position)
    }

}