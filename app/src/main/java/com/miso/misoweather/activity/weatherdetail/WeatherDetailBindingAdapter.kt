package com.miso.misoweather.activity.weatherdetail

import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.model.dto.forecast.brief.ForecastBriefData
import com.miso.misoweather.model.dto.forecast.currentAir.CurrentAirData
import com.miso.misoweather.model.dto.forecast.daily.DailyForecast
import com.miso.misoweather.model.dto.forecast.daily.DailyForecastData
import com.miso.misoweather.model.dto.forecast.hourly.HourlyForecast
import com.miso.misoweather.model.dto.forecast.hourly.HourlyForecastData
import java.lang.Exception
import java.time.ZoneId
import java.time.ZonedDateTime

object WeatherDetailBindingAdapter {

    @BindingAdapter("minDegreeText")
    @JvmStatic
    fun setMinDegreeText(view: TextView, data: ForecastBriefData?) {
        data?.let {
            view.text = "${CommonUtil.toIntString(data.temperatureMin)}˚"
        }
    }

    @BindingAdapter("maxDegreeText")
    @JvmStatic
    fun setmaxDegree(view: TextView, data: ForecastBriefData?) {
        data?.let {
            view.text = "${CommonUtil.toIntString(data.temperatureMax)}˚"
        }
    }

    @BindingAdapter("degreeText")
    @JvmStatic
    fun setdegree(view: TextView, data: ForecastBriefData?) {
        data?.let {
            view.text = "${CommonUtil.toIntString(it.temperature)}˚"
        }
    }

    @BindingAdapter("emojiHumidText")
    @JvmStatic
    fun setemojiHumid(view: TextView, data: ForecastBriefData?) {
        data?.let {
            view.text = "${data.humidityIcon}"
        }
    }

    @BindingAdapter("degreeHumidText")
    @JvmStatic
    fun setdegreeHumid(view: TextView, data: ForecastBriefData?) {
        data?.let {
            view.text = "${data.humidity + "%"}"
        }
    }

    @BindingAdapter("emojiWindText")
    @JvmStatic
    fun setemojiWind(view: TextView, data: ForecastBriefData?) {
        data?.let {
            view.text = "${data.windSpeedIcon}"
        }
    }

    @BindingAdapter("weatherEmojiText")
    @JvmStatic
    fun setweatherEmoji(view: TextView, data: ForecastBriefData?) {
        data?.let {
            view.text = "${data.weather}"
        }
    }

    @BindingAdapter("degreeWindText")
    @JvmStatic
    fun setdegreeWind(view: TextView, data: ForecastBriefData?) {
        data?.let {
            view.text = "${data.windSpeedComment}"
        }
    }

    @BindingAdapter("emojiRainText")
    @JvmStatic
    fun setemojiRain(view: TextView, data: DailyForecastData?) {
        data?.let {
            view.text = "${data.popIcon}"
        }
    }

    @BindingAdapter("degreeRainText")
    @JvmStatic
    fun setDegreeRain(view: TextView, data: DailyForecastData?) {
        data?.let {
            view.text = "${data.pop + "%"}"
        }
    }

    @BindingAdapter("degreeRainOnHourText")
    @JvmStatic
    fun setDegreeRainOnHour(view: TextView, data: DailyForecastData?) {
        data?.let {
            view.text = "${"시간당 ${getDegreeRainOnHour(data)}mm"}"
        }
    }


    private fun getDegreeRainOnHour(data: DailyForecastData): String {
        try {
            val rain = data.rain
            val snow = data.snow

            return if (rain.isNullOrBlank() && snow.isNullOrBlank())
                "0"
            else if (rain.isNullOrBlank())
                snow
            else if (snow.isNullOrBlank())
                rain
            else {
                if (rain.toInt() > snow.toInt())
                    rain
                else
                    snow
            }
        } catch (e: Exception) {
            Log.e("getDegreeRainOnHour", e.stackTraceToString())
            return "0"
        }
    }


    @BindingAdapter("emojiDustText")
    @JvmStatic
    fun setemojiDust(view: TextView, data: CurrentAirData?) {
        data?.let {
            view.text = "${data.fineDustIcon}"
        }
    }

    @BindingAdapter("degreeDustText")
    @JvmStatic
    fun setdegreeDust(view: TextView, data: CurrentAirData?) {
        data?.let {
            view.text = "${data.fineDust}"
        }
    }

    @BindingAdapter("gradeDustText")
    @JvmStatic
    fun setgradeDust(view: TextView, data: CurrentAirData?) {
        data?.let {
            view.text = "${data.fineDustGrade}"
        }
    }

    @BindingAdapter("emojiUltraDustText")
    @JvmStatic
    fun setemojiUltraDust(view: TextView, data: CurrentAirData?) {
        data?.let {
            view.text = "${data.ultraFineDustIcon}"
        }
    }

    @BindingAdapter("degreeUltraDustText")
    @JvmStatic
    fun setdegreeUltraDust(view: TextView, data: CurrentAirData?) {
        data?.let {
            view.text = "${data.ultraFineDust}"
        }
    }

    @BindingAdapter("gradeUltraDustText")
    @JvmStatic
    fun setgradeUltraDust(view: TextView, data: CurrentAirData?) {
        data?.let {
            view.text = "${data.ultraFineDustGrade}"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("bindWeatherOnDayData")
    @JvmStatic
    fun setWeatherOnDayData(view: RecyclerView, data: DailyForecastData?) {
        data?.let {
            view.adapter =
                RecyclerForecastOnDayAdapter(data.dailyForecastList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("bindWeatherOnTimeData")
    @JvmStatic
    fun setWeatherOnHourlyData(view: RecyclerView, data: HourlyForecastData?) {
        data?.let {
            view.adapter =
                RecyclerForecastOnTimeAdapter(data.hourlyForecastList)
        }
    }
}