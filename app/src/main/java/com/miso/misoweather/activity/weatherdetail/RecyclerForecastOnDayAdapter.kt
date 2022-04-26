package com.miso.misoweather.activity.weatherdetail

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ListWeatherDayBinding
import com.miso.misoweather.model.dto.forecast.daily.DailyForecast

@RequiresApi(Build.VERSION_CODES.O)
class RecyclerForecastOnDayAdapter(
    private val forecasts: List<DailyForecast>
) :
    RecyclerView.Adapter<RecyclerForecastOnDayAdapter.Holder>() {

    override fun getItemCount(): Int {
        return forecasts.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position,forecasts[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_weather_day, parent, false)
        return Holder(ListWeatherDayBinding.bind(view))
    }

    class Holder(val binding: ListWeatherDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position:Int,forecast:DailyForecast)
        {
           binding.forecast = forecast
            binding.position = position
        }
    }

}