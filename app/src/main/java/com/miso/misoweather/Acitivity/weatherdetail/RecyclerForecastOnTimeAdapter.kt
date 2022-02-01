package com.miso.misoweather.Acitivity.weatherdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.model.DTO.Forecast.Forecast

class RecyclerForecastOnTimeAdapter(var context: Context, var forecasts: List<Forecast>) :
    RecyclerView.Adapter<RecyclerForecastOnTimeAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return forecasts.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.degree.text = forecasts.get(position).temperature+"˚"
        holder.emoji.text = forecasts.get(position).sky
        holder.time.text = forecasts.get(position).hour+"시"
        viewHolders.add(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_weather_on_time, parent, false)
        return Holder(view)
    }

    fun getForecastOnHour(hour:Int):Forecast
    {
        return forecasts.first(){it.hour.toInt()==hour}
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var emoji = itemView.findViewById<TextView>(R.id.txt_weather_emoji)
        var degree = itemView.findViewById<TextView>(R.id.txt_weather_degree)
        var time = itemView.findViewById<TextView>(R.id.txt_time)
    }

}