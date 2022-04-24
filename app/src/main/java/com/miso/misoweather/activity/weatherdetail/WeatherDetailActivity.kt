package com.miso.misoweather.activity.weatherdetail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityWeatherMainBinding
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.activity.selectAnswer.SelectSurveyAnswerActivity
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefData
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.Forecast.CurrentAir.CurrentAirData
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastData
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.ZoneId
import java.time.ZonedDateTime

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class WeatherDetailActivity : MisoActivity() {
    private val viewModel: WeatherDetailViewModel by viewModels()
    private lateinit var binding: ActivityWeatherMainBinding
    private lateinit var btnBack: ImageButton
    private lateinit var chatLayout: ConstraintLayout
    private lateinit var txtLocation: TextView
    private lateinit var txtWeatherEmoji: TextView
    private lateinit var txtDegree: TextView
    private lateinit var txtMinDegree: TextView
    private lateinit var txtMaxDegree: TextView
    private lateinit var btnChat: ImageButton
    private lateinit var recyclerWeatherOnTime: RecyclerView
    private lateinit var recyclerWeaterOnDay: RecyclerView
    private lateinit var weatherOnTimeAdapter: RecyclerForecastOnTimeAdapter
    private lateinit var weatherOnDayAdapter: RecyclerForecastOnDayAdapter
    private lateinit var txtEmojiRain: TextView
    private lateinit var txtDegreeRain: TextView
    private lateinit var txtDegreeRainOnHour: TextView
    private lateinit var txtEmojiWind: TextView
    private lateinit var txtDegreeWind: TextView
    private lateinit var txtEmojiHumid: TextView
    private lateinit var txtDegreeHumid: TextView
    private lateinit var txtForecastDay: TextView
    private lateinit var txtEmojiDust: TextView
    private lateinit var txtDegreeDust: TextView
    private lateinit var txtGradeDust: TextView
    private lateinit var txtEmojiUltraDust: TextView
    private lateinit var txtDegreeUltraDust: TextView
    private lateinit var txtGradeUltraDust: TextView
    private lateinit var briefForecastData: ForecastBriefData
    private lateinit var dailyForecastData: DailyForecastData
    private lateinit var hourlyForecastData: HourlyForecastData
    private lateinit var currentAirData: CurrentAirData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setupWeatherInfo()
    }


    private fun setupWeatherInfo() {
        setForecastInfo()
        setupBriefForecastData()
        setupDailyForecastData()
        setupHourlyForecastData()
        setupCurrentAir()
    }

    private fun setupBriefForecastViews() {
        txtMinDegree.text = CommonUtil.toIntString(briefForecastData.temperatureMin) + "˚"
        txtMaxDegree.text = CommonUtil.toIntString(briefForecastData.temperatureMax) + "˚"
        txtDegree.text = CommonUtil.toIntString(briefForecastData.temperature) + "˚"
        txtEmojiHumid.text = briefForecastData.humidityIcon
        txtDegreeHumid.text = briefForecastData.humidity + "%"
        txtEmojiWind.text = briefForecastData.windSpeedIcon
        txtWeatherEmoji.text = briefForecastData.weather
        txtDegreeWind.text = briefForecastData.windSpeedComment
    }

    private fun setupDailyForecastViews() {
        txtEmojiRain.text = dailyForecastData.popIcon
        txtDegreeRain.text = dailyForecastData.pop + "%"
        txtDegreeRainOnHour.text = "시간당 ${getDegreeRainOnHour()}mm"
        setupWeatherOnDayRecycler()
    }

    private fun setupCurrentAirViews() {
        txtEmojiDust.text = currentAirData.fineDustIcon
        txtDegreeDust.text = currentAirData.fineDust
        txtGradeDust.text = currentAirData.fineDustGrade
        txtEmojiUltraDust.text = currentAirData.ultraFineDustIcon
        txtDegreeUltraDust.text = currentAirData.ultraFineDust
        txtGradeUltraDust.text = currentAirData.ultraFineDustGrade
    }

    private fun setupBriefForecastData() {
        lifecycleScope.launch {
            viewModel.getBriefForecast {
                try {
                    if (it.isSuccessful) {
                        val briefResponseDto = it.body() as ForecastBriefResponseDto
                        briefForecastData = briefResponseDto.data
                        setupBriefForecastViews()
                        Log.i("forecastBriefResponse", "성공")
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } catch (e: Exception) {
                    if (e.message!!.isNotBlank())
                        Log.e("forecastBriefResponse", e.message.toString())
                    Log.e("forecastBriefResponse", e.stackTraceToString())
                }
            }
        }
    }

    private fun setupDailyForecastData() {
        lifecycleScope.launch {
            viewModel.getDailyForecast {
                try {
                    if (it.isSuccessful) {
                        val dailyForecastResponse = it.body()!!
                        dailyForecastData = dailyForecastResponse.data
                        setupDailyForecastViews()
                        Log.i("setupDailyForecastData", "성공")
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@WeatherDetailActivity,
                        "일 별 예보를 불러오는데 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (e.message!!.isNotBlank())
                        Log.e("setupDailyForecastData", e.message.toString())
                    Log.e("setupDailyForecastData", e.stackTraceToString())
                }
            }
        }
    }

    private fun setupHourlyForecastData() {
        lifecycleScope.launch {
            viewModel.getHourlyForecast {
                try {
                    if (it.isSuccessful) {
                        val hourlyForecastResponse = it.body()!!
                        hourlyForecastData = hourlyForecastResponse.data
                        setupWeatherOnTimeRecycler()
                        Log.i("setupHourlyForecastData", "성공")
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@WeatherDetailActivity,
                        "시간 별 예보를 불러오는데 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (e.message!!.isNotBlank())
                        Log.e("setupHourlyForecastData", e.message.toString())
                    Log.e("setupHourlyForecastData", e.stackTraceToString())
                }
            }
        }
    }

    private fun setupCurrentAir() {
        lifecycleScope.launch {
            viewModel.getCurrentAir {
                try {
                    if (it.isSuccessful) {
                        val currentAirResponse = it.body()!!
                        currentAirData = currentAirResponse.data
                        setupCurrentAirViews()
                        Log.i("currentAirData", "성공")
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@WeatherDetailActivity,
                        "미세먼지 정보를 불러오는데 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (e.message!!.isNotBlank())
                        Log.e("currentAirData", e.message.toString())
                    Log.e("currentAirData", e.stackTraceToString())
                }
            }
        }
    }


    private fun initializeViews() {
        chatLayout = binding.chatLayout
        txtLocation = binding.txtLocation
        txtWeatherEmoji = binding.txtWeatherEmoji
        txtDegree = binding.txtDegree
        txtMinDegree = binding.txtMinDegree
        txtMaxDegree = binding.txtMaxDegree
        btnChat = binding.imgbtnChat
        txtEmojiRain = binding.txtImogeRain
        txtDegreeRain = binding.txtDegreeRain
        txtDegreeRainOnHour = binding.txtRainDegreeOnHour
        txtEmojiWind = binding.txtEmojiWindSpeed
        txtDegreeWind = binding.txtDegreeWind
        txtEmojiHumid = binding.txtEmojiHumid
        txtDegreeHumid = binding.txtDegreeHumid
        txtForecastDay = binding.txtTitleForecastDay
        txtEmojiDust = binding.txtEmojiDust
        txtDegreeDust = binding.txtDegreeDust
        txtGradeDust = binding.txtDustGrade
        txtEmojiUltraDust = binding.txtEmojiSuperdust
        txtDegreeUltraDust = binding.txtDegreeSuperdust
        txtGradeUltraDust = binding.txtSuperdustGrade
        txtForecastDay.text = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).dayOfMonth.toString()
        btnBack = binding.imgbtnBack
        btnBack.setOnClickListener()
        {
            doBack()
        }
        chatLayout.setOnClickListener()
        {
            goToChatMainActivity()
        }
        recyclerWeatherOnTime = binding.recylcerWeatherOnTIme
        recyclerWeaterOnDay = binding.recyclerForecast
    }

    override fun doBack() {
        startActivity(Intent(this, HomeActivity::class.java))
        transferToBack()
        finish()
    }

    private fun goToChatMainActivity() {
        if (!this.intent.getBooleanExtra("isTodaySurveyed", true)) {
            val intent = Intent(this, SelectSurveyAnswerActivity::class.java)
            intent.apply {
                intent.putExtra("isFirstSurvey", true)
                intent.putExtra("previousActivity", "Weather")
            }
            startActivity(intent)
            transferToNext()
            finish()
        } else {
            val intent = Intent(this, ChatMainActivity::class.java)
            intent.putExtra("previousActivity", "Weather")
            startActivity(intent)
            transferToNext()
            finish()
        }
    }


    private fun setForecastInfo() {
        val midScaleString = if (viewModel.midScale.equals("선택 안 함")) "전체" else viewModel.midScale
        val smallScaleString =
            if (midScaleString.equals("전체"))
                ""
            else
                if (viewModel.smallScale.equals("선택 안 함"))
                    "전체"
                else
                    viewModel.smallScale

        txtLocation.text = viewModel.bigScale + " " + midScaleString + " " + smallScaleString
    }

    private fun getDegreeRainOnHour(): String {
        try {
            val rain = dailyForecastData.rain
            val snow = dailyForecastData.snow

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

    private fun setupWeatherOnDayRecycler() {
        weatherOnDayAdapter =
            RecyclerForecastOnDayAdapter(dailyForecastData.dailyForecastList)
        recyclerWeaterOnDay.apply {
            adapter = weatherOnDayAdapter
            layoutManager =
                LinearLayoutManager(this@WeatherDetailActivity, RecyclerView.VERTICAL, false)
        }
    }

    private fun setupWeatherOnTimeRecycler() {
        weatherOnTimeAdapter =
            RecyclerForecastOnTimeAdapter(hourlyForecastData.hourlyForecastList)
        recyclerWeatherOnTime.apply {
            adapter = weatherOnTimeAdapter
            layoutManager =
                LinearLayoutManager(this@WeatherDetailActivity, RecyclerView.HORIZONTAL, false)
        }
    }
}