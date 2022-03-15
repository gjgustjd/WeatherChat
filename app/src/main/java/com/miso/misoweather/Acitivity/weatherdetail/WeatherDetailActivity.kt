package com.miso.misoweather.Acitivity.weatherdetail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityWeatherMainBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.selectAnswer.SelectSurveyAnswerActivity
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefData
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastData
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastData
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.MisoRepository
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class WeatherDetailActivity : MisoActivity() {
    lateinit var binding: ActivityWeatherMainBinding
    lateinit var btnBack: ImageButton
    lateinit var region: Region
    lateinit var chatLayout: ConstraintLayout
    lateinit var txtLocation: TextView
    lateinit var txtWeatherEmoji: TextView
    lateinit var txtDegree: TextView
    lateinit var txtMinDegree: TextView
    lateinit var txtMaxDegree: TextView
    lateinit var btnChat: ImageButton
    lateinit var recyclerWeatherOnTime: RecyclerView
    lateinit var recyclerWeaterOnDay: RecyclerView
    lateinit var weatherOnTimeAdapter: RecyclerForecastOnTimeAdapter
    lateinit var weatherOnDayAdapter: RecyclerForecastOnDayAdapter
    lateinit var txtEmojiRain: TextView
    lateinit var txtDegreeRain: TextView
    lateinit var txtDegreeRainOnHour: TextView
    lateinit var txtEmojiWind: TextView
    lateinit var txtDegreeWind: TextView
    lateinit var txtForecastDay: TextView
    lateinit var viewModel: WeatherDetailViewModel
    lateinit var bigScale: String
    lateinit var midScale: String
    lateinit var smallScale: String
    lateinit var isSurveyed: String
    lateinit var lastSurveyedDate: String
    lateinit var defaultRegionId: String
    var isAllInitialized = false
    lateinit var briefForecastData: ForecastBriefData
    lateinit var dailyForecastData: DailyForecastData
    lateinit var hourlyForecastData: HourlyForecastData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProperties()
    }

    fun initializeProperties() {
        fun checkinitializedAll() {
            if (!isAllInitialized) {
                if (
                    this::isSurveyed.isInitialized &&
                    this::defaultRegionId.isInitialized &&
                    this::lastSurveyedDate.isInitialized &&
                    this::bigScale.isInitialized &&
                    this::midScale.isInitialized &&
                    this::smallScale.isInitialized
                ) {
                    initializeViews()
                    setForecastInfo()
                    isAllInitialized = true
                }
            }
        }
        viewModel = WeatherDetailViewModel(MisoRepository.getInstance(applicationContext))
        viewModel.updateProperties()
        viewModel.isSurveyed.observe(this, {
            isSurveyed = it!!
            checkinitializedAll()
        })
        viewModel.lastSurveyedDate.observe(this, {
            lastSurveyedDate = it!!
            checkinitializedAll()
        })
        viewModel.defaultRegionId.observe(this, {
            defaultRegionId = it!!
            checkinitializedAll()
        })
        viewModel.bigScale.observe(this, {
            bigScale = it!!
            checkinitializedAll()
        })
        viewModel.midScale.observe(this, {
            midScale = it!!
            checkinitializedAll()
        })
        viewModel.smallScale.observe(this, {
            smallScale = it!!
            checkinitializedAll()
        })
    }

    fun initializeViews() {
        briefForecastData = intent.getSerializableExtra("briefForecast") as ForecastBriefData
        dailyForecastData = intent.getSerializableExtra("dailyForecast") as DailyForecastData
        hourlyForecastData = intent.getSerializableExtra("hourlyForecast") as HourlyForecastData
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
        txtForecastDay = binding.txtTitleForecastDay
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

    fun goToChatMainActivity() {
        var currentDate =
            ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
        if (!isSurveyed.equals("true") || !lastSurveyedDate.equals(currentDate)) {
            var intent = Intent(this, SelectSurveyAnswerActivity::class.java)
            intent.putExtra("isFirstSurvey", true)
            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToNext()
            finish()
        } else {
            var intent = Intent(this, ChatMainActivity::class.java)
            intent.putExtra("previousActivity", "Weather")
            startActivity(intent)
            transferToNext()
            finish()
        }
    }


    fun setForecastInfo() {
        val midScaleString = if (midScale.equals("선택 안 함")) "전체" else midScale
        val smallScaleString =
            if (midScaleString.equals("전체"))
                ""
            else
                if (smallScale.equals("선택 안 함"))
                    "전체"
                else
                    smallScale

        txtLocation.text = bigScale + " " + midScaleString + " " + smallScaleString
        txtMinDegree.text = briefForecastData.temperatureMin.split(".")[0] + "˚"
        txtMaxDegree.text = briefForecastData.temperatureMax.split(".")[0] + "˚"
        txtDegree.text = briefForecastData.temperature.split(".")[0] + "˚"
        txtWeatherEmoji.text = briefForecastData.weather
//        txtEmojiRain.text = forecastdetailInfo.rainSnow
//        txtDegreeRain.text = forecastdetailInfo.rainSnowPossibility + "%"
//        txtDegreeRainOnHour.text = forecastdetailInfo.rainSnowValue
//        txtDegreeWind.text = getWindDegree(forecastdetailInfo.windSpeed)
//        txtEmojiWind.text = forecastdetailInfo.windSpeed
        setupWeatherOnTimeRecycler()
        setupWeatherOnDayRecycler()
    }

    fun getWindDegree(emoji: String): String {
        val degrees: Array<String> = resources.getStringArray(R.array.wind_degree)
        val emojies: Array<String> = resources.getStringArray(R.array.wind_emoji)

        return degrees.get(emojies.indexOf(emoji))
    }

    fun setupWeatherOnDayRecycler() {
        weatherOnDayAdapter =
            RecyclerForecastOnDayAdapter(this, dailyForecastData.dailyForecastList)
        recyclerWeaterOnDay.adapter = weatherOnDayAdapter
        recyclerWeaterOnDay.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }


    fun setupWeatherOnTimeRecycler() {
        weatherOnTimeAdapter =
            RecyclerForecastOnTimeAdapter(this, hourlyForecastData.hourlyForecastList)
        recyclerWeatherOnTime.adapter = weatherOnTimeAdapter
        recyclerWeatherOnTime.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }


}