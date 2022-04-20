package com.miso.misoweather.Acitivity.weatherdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.model.DTO.Forecast.Forecast
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecast

class RecyclerForecastOnTimeAdapter(
    private val forecasts: List<HourlyForecast>
) :
    RecyclerView.Adapter<RecyclerForecastOnTimeAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return forecasts.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.degree.text = CommonUtil.toIntString(forecasts.get(position).temperature) + "˚"
        holder.emoji.text = forecasts[position].weather
        holder.time.text = forecasts[position].forecastTime
            .split("T")[1]
            .split(":")[0] + "시"
        viewHolders.add(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_weather_on_time, parent, false)
        return Holder(view)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emoji = itemView.findViewById<TextView>(R.id.txt_weather_emoji)
        val degree = itemView.findViewById<TextView>(R.id.txt_weather_degree)
        val time = itemView.findViewById<TextView>(R.id.txt_time)
    }

}