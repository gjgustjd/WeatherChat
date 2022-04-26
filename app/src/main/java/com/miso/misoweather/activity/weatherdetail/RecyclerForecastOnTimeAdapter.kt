package com.miso.misoweather.activity.weatherdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ListWeatherOnTimeBinding
import com.miso.misoweather.model.dto.forecast.hourly.HourlyForecast

class RecyclerForecastOnTimeAdapter(
    private val forecasts: List<HourlyForecast>
) :
    RecyclerView.Adapter<RecyclerForecastOnTimeAdapter.Holder>() {

    override fun getItemCount(): Int {
        return forecasts.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(forecasts[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_weather_on_time, parent, false)
        return Holder(ListWeatherOnTimeBinding.bind(view))
    }

    class Holder(val binding: ListWeatherOnTimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(forecast: HourlyForecast) {
            binding.forecast = forecast
        }
    }

}