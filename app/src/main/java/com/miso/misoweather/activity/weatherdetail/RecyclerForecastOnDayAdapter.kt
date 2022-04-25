package com.miso.misoweather.activity.weatherdetail

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.databinding.ListWeatherDayBinding
import com.miso.misoweather.model.dto.forecast.daily.DailyForecast
import java.time.ZoneId
import java.time.ZonedDateTime

@RequiresApi(Build.VERSION_CODES.O)
class RecyclerForecastOnDayAdapter(
    private val forecasts: List<DailyForecast>
) :
    RecyclerView.Adapter<RecyclerForecastOnDayAdapter.Holder>() {

    private val viewHolders = arrayListOf<Holder>()
    private val dayNameList = listOf("월", "화", "수", "목", "금", "토", "일")

    private fun getDayString(position: Int): String {
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

    override fun getItemCount(): Int {
        return forecasts.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.apply {
            degreeMax.text = CommonUtil.toIntString(forecasts[position].maxTemperature) + "˚"
            degreeMin.text = forecasts[position].minTemperature.split(".")[0] + "˚"
            emoji.text = forecasts[position].weather
            popEmoji.text = forecasts[position].popIcon
            popDegree.text = forecasts[position].pop + "%"
            val forecastDate = forecasts[position].forecastTime.split("T")[0]
            time.text = forecastDate.split("-")[1] + "/" + forecastDate.split("-")[2]
            day.text = getDayString(position)
        }
        Log.i("dayOfWeek", ZonedDateTime.now(ZoneId.of("Asia/Seoul")).dayOfWeek.toString())
        viewHolders.add(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_weather_day, parent, false)
        return Holder(ListWeatherDayBinding.bind(view))
    }

    class Holder(itemView: ListWeatherDayBinding) : RecyclerView.ViewHolder(itemView.root) {
        val emoji = itemView.txtWeatherEmoji
        val popEmoji = itemView.txtPopEmoji
        val popDegree = itemView.txtPopDegree
        val degreeMin = itemView.txtDegreeMin
        val degreeMax = itemView.txtDegreeMax
        val day = itemView.txtDay
        val time = itemView.txtDate
    }

}