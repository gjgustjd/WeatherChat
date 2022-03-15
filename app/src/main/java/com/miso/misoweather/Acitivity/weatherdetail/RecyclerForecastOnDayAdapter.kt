package com.miso.misoweather.Acitivity.weatherdetail

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ListWeatherDayBinding
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecast
import com.miso.misoweather.model.DTO.Forecast.Forecast
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecast
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class RecyclerForecastOnDayAdapter(var context: Context, var forecasts: List<DailyForecast>) :
    RecyclerView.Adapter<RecyclerForecastOnDayAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()
    val dayNameList = listOf("월", "화", "수", "목", "금", "토", "일")

    fun getDayString(position: Int): String {
        var dayPosition = LocalDateTime.now().dayOfWeek.value
        if (position == 0) {
            return "오늘"
        } else {
            val sumPosition = dayPosition + position
            if (sumPosition > 7) {
                val index = (sumPosition % 7) - 1
                return dayNameList.get(index)
            } else
                return dayNameList.get(sumPosition - 1)
        }
    }

    override fun getItemCount(): Int {
        return forecasts.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.degreeMax.text = forecasts[position].maxTemperature.split(".")[0] + "˚"
        holder.degreeMin.text = forecasts[position].minTemperature.split(".")[0] + "˚"
        holder.emoji.text = forecasts[position].weather
        var forecastDate = forecasts[position].forecastTime.split("T")[0]
        holder.time.text = forecastDate.split("-")[1] + "/" + forecastDate.split("-")[2]
        holder.day.text = getDayString(position)
        Log.i("dayOfWeek", LocalDateTime.now().dayOfWeek.toString())
        viewHolders.add(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_weather_day, parent, false)
        return Holder(ListWeatherDayBinding.bind(view))
    }

    class Holder(itemView: ListWeatherDayBinding) : RecyclerView.ViewHolder(itemView.root) {
        var emoji = itemView.txtWeatherEmoji
        var degreeMin = itemView.txtDegreeMin
        var degreeMax = itemView.txtDegreeMax
        var day = itemView.txtDay
        var time = itemView.txtDate
    }

}