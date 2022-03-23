package com.miso.misoweather.Acitivity.weatherdetail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefData
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.Forecast.CurrentAir.CurrentAirData
import com.miso.misoweather.model.DTO.Forecast.CurrentAir.CurrentAirResponseDto
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastData
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastResponseDto
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastData
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastResponseDto
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception
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
    lateinit var txtEmojiHumid: TextView
    lateinit var txtDegreeHumid: TextView
    lateinit var txtForecastDay: TextView
    lateinit var txtEmojiDust: TextView
    lateinit var txtDegreeDust: TextView
    lateinit var txtGradeDust: TextView
    lateinit var txtEmojiUltraDust: TextView
    lateinit var txtDegreeUltraDust: TextView
    lateinit var txtGradeUltraDust: TextView
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
    lateinit var currentAirData: CurrentAirData


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
                    this::smallScale.isInitialized &&
                    this::briefForecastData.isInitialized &&
                    this::dailyForecastData.isInitialized &&
                    this::hourlyForecastData.isInitialized &&
                    this::currentAirData.isInitialized
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
        viewModel.forecastBriefResponse.observe(this, {
            try {
                if (it is Response<*>) {
                    if (it.isSuccessful) {
                        var briefResponseDto = it.body() as ForecastBriefResponseDto
                        briefForecastData = briefResponseDto.data
                        checkinitializedAll()
                        Log.i("forecastBriefResponse", "성공")
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } else {
                    if (it is String)
                        throw Exception(it)
                    else if (it is Throwable)
                        throw it
                }
            } catch (e: Exception) {
                if (e.message!!.isNotBlank())
                    Log.e("forecastBriefResponse", e.message.toString())
                Log.e("forecastBriefResponse", e.stackTraceToString())
            }
        })
        viewModel.dailyForecastResponse.observe(this, {
            try {
                if (it is Response<*>) {
                    if (it.isSuccessful) {
                        var dailyForecastResponse = it.body() as DailyForecastResponseDto
                        dailyForecastData = dailyForecastResponse.data
                        checkinitializedAll()
                        Log.i("dailyForecastResponse", "성공")
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } else {
                    if (it is String)
                        throw Exception(it)
                    else if (it is Throwable)
                        throw it
                }
            } catch (e: Exception) {
                if (e.message!!.isNotBlank())
                    Log.e("dailyForecastResponse", e.message.toString())
                Log.e("dailyForecastResponse", e.stackTraceToString())
            }
        })
        viewModel.hourlyForecastResponse.observe(this, {
            try {
                if (it is Response<*>) {
                    if (it.isSuccessful) {
                        var hourlyForecastResponse = it.body() as HourlyForecastResponseDto
                        hourlyForecastData = hourlyForecastResponse.data
                        checkinitializedAll()
                        Log.i("hourlyForecastData", "성공")
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } else {
                    if (it is String)
                        throw Exception(it)
                    else if (it is Throwable)
                        throw it
                }
            } catch (e: Exception) {
                if (e.message!!.isNotBlank())
                    Log.e("hourlyForecastData", e.message.toString())
                Log.e("hourlyForecastData", e.stackTraceToString())
            }
        })
        viewModel.currentAirResponse.observe(this, {
            try {
                if (it is Response<*>) {
                    if (it.isSuccessful) {
                        var currentAirResponse = it.body() as CurrentAirResponseDto
                        currentAirData = currentAirResponse.data
                        checkinitializedAll()
                        Log.i("currentAirData", "성공")
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } else {
                    if (it is String)
                        throw Exception(it)
                    else if (it is Throwable)
                        throw it
                }
            } catch (e: Exception) {
                if (e.message!!.isNotBlank())
                    Log.e("currentAirData", e.message.toString())
                Log.e("currentAirData", e.stackTraceToString())
            }
        })
    }

    fun initializeViews() {
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

    fun goToChatMainActivity() {
        var currentDate =
            ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
        if (!isSurveyed.equals("true") || !lastSurveyedDate.equals(currentDate)) {
            var intent = Intent(this, SelectSurveyAnswerActivity::class.java)
            intent.putExtra("isFirstSurvey", true)
            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToNext()
            finish()
        } else {
            var intent = Intent(this, ChatMainActivity::class.java)
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
        txtMinDegree.text = CommonUtil.toIntString(briefForecastData.temperatureMin) + "˚"
        txtMaxDegree.text = CommonUtil.toIntString(briefForecastData.temperatureMax) + "˚"
        txtDegree.text = CommonUtil.toIntString(briefForecastData.temperature) + "˚"
        txtWeatherEmoji.text = briefForecastData.weather
        txtEmojiRain.text = dailyForecastData.popIcon
        txtDegreeRain.text = dailyForecastData.pop + "%"
        txtDegreeRainOnHour.text = getDegreeRainOnHour() + " mm"
        txtDegreeWind.text = getWindDegree(briefForecastData.windSpeedIcon)
        txtEmojiHumid.text = briefForecastData.humidityIcon
        txtDegreeHumid.text = briefForecastData.humidity + "%"
        txtEmojiWind.text = briefForecastData.windSpeedIcon
        txtEmojiDust.text = currentAirData.fineDustIcon
        txtDegreeDust.text = currentAirData.fineDust
        txtGradeDust.text = currentAirData.fineDustGrade
        txtEmojiUltraDust.text = currentAirData.ultraFineDustIcon
        txtDegreeUltraDust.text = currentAirData.ultraFineDust
        txtGradeUltraDust.text = currentAirData.ultraFineDustGrade
        setupWeatherOnTimeRecycler()
        setupWeatherOnDayRecycler()
    }

    fun getDegreeRainOnHour(): String {
        try {
            var rain = dailyForecastData.rain
            var snow = dailyForecastData.snow

            if (rain.isNullOrBlank() && snow.isNullOrBlank())
                return "0"
            else if (rain.isNullOrBlank())
                return snow
            else if (snow.isNullOrBlank())
                return rain
            else {
                if (rain.toInt() > snow.toInt())
                    return rain
                else
                    return snow
            }
        } catch (e: Exception) {
            Log.e("getDegreeRainOnHour", e.stackTraceToString())
            return "0"
        }
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