package com.miso.misoweather.Acitivity.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityHomeBinding
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.Acitivity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.Acitivity.mypage.MyPageActivity
import com.miso.misoweather.Acitivity.selectAnswer.SelectSurveyAnswerActivity
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import com.miso.misoweather.Dialog.GeneralConfirmDialog
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefData
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastData
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastResponseDto
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastData
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastResponseDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class HomeActivity : MisoActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var weatherLayout: ConstraintLayout
    lateinit var txtNickName: TextView
    lateinit var txtEmoji: TextView
    lateinit var txtLocation: TextView
    lateinit var txtWeatherEmoji: TextView
    lateinit var txtWeatherDegree: TextView
    lateinit var btnShowWeatherDetail: ImageButton
    lateinit var btnProfile: ImageButton
    lateinit var btngoToSurvey: ImageButton
    lateinit var recyclerChat: RecyclerView
    lateinit var recyclerChatAdapter: RecyclerChatsAdapter
    lateinit var txtFirstAnswer: TextView
    lateinit var txtSecondAnswer: TextView
    lateinit var txtThirdAnswer: TextView
    lateinit var txtFirstRatio: TextView
    lateinit var txtSecondRatio: TextView
    lateinit var txtThirdRatio: TextView
    lateinit var imgIconCheckFirst: ImageView
    lateinit var imgbtnChangeLocaion: ImageButton
    lateinit var firstProgressLayout: ConstraintLayout
    lateinit var secondProgressLayout: ConstraintLayout
    lateinit var thirdProgressLayout: ConstraintLayout
    lateinit var chartLayout: ConstraintLayout
    lateinit var txtEmptyChart: TextView

    lateinit var viewModel: HomeViewModel
    lateinit var repository: MisoRepository

    lateinit var isSurveyed: String
    lateinit var lastSurveyedDate: String
    lateinit var defaultRegionId: String
    lateinit var misoToken: String
    lateinit var bigScale: String
    lateinit var midScale: String
    lateinit var smallScale: String
    var hourlyForecastData: HourlyForecastData? = null
    var dailyForecastData: DailyForecastData? = null
    var briefForecastData: ForecastBriefData? = null

    var isAllInitialized = false
    var isWeatherLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        initializeProperties()
    }

    fun setupData() {
        getUserInfo()
        getCommentList()
        loadWeatherInfo()
        Log.i("setupData", "Launched")
    }

    fun loadWeatherInfo() {
        viewModel.loadWeatherInfo(defaultRegionId.toInt())
        viewModel.isWeatherLoaded.observe(this, {
            isWeatherLoaded = it
            if (isWeatherLoaded) {
                getBriefForecast()
                getDailyForecast()
                getHourlyForecast()
            } else {
                Toast.makeText(this, "날씨 정보 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun initializeProperties() {
        fun checkinitializedAll() {
            if (!isAllInitialized) {
                if (
                    this::isSurveyed.isInitialized &&
                    this::lastSurveyedDate.isInitialized &&
                    this::defaultRegionId.isInitialized &&
                    this::misoToken.isInitialized &&
                    this::bigScale.isInitialized &&
                    this::midScale.isInitialized &&
                    this::smallScale.isInitialized
                ) {
                    setupData()
                    isAllInitialized = true
                }
            }
        }
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
        viewModel.misoToken.observe(this, {
            misoToken = it!!
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

        Log.i("initializeProperties", "Launched")
    }

    fun initializeViews() {
        repository = MisoRepository.getInstance(applicationContext)
        viewModel = HomeViewModel(repository)
        weatherLayout = binding.weatherLayout
        txtNickName = binding.txtNickname
        txtEmoji = binding.txtEmoji
        txtLocation = binding.txtLocation
        txtWeatherDegree = binding.txtDegree
        txtWeatherEmoji = binding.txtWeatherEmoji
        btnShowWeatherDetail = binding.imgbtnShowWeather
        btnProfile = binding.imgbtnProfile
        btngoToSurvey = binding.imgbtnSurvey
        txtFirstAnswer = binding.txtAnswerFirst
        txtSecondAnswer = binding.txtAnswerSecond
        txtThirdAnswer = binding.txtAnswerThird
        txtFirstRatio = binding.txtRatioFirst
        txtSecondRatio = binding.txtRatioSecond
        txtThirdRatio = binding.txtRatioThird
        imgIconCheckFirst = binding.imgIconFirst
        imgbtnChangeLocaion = binding.imgbtnChangeLocation
        firstProgressLayout = binding.itemFirstLayout
        secondProgressLayout = binding.itemSecondLayout
        thirdProgressLayout = binding.itemThirdLayout
        chartLayout = binding.chartLayout
        txtEmptyChart = binding.txtEmptyChart

        chartLayout.setOnClickListener()
        {
            goToChatMainActivity()
        }

        btngoToSurvey.setOnClickListener()
        {
            goToChatMainActivity()
        }
        weatherLayout.setOnClickListener()
        {
            if (dailyForecastData != null &&
                briefForecastData != null &&
                dailyForecastData != null
            ) {
                var intent = Intent(this, WeatherDetailActivity::class.java)
                intent.putExtra("briefForecast", briefForecastData)
                intent.putExtra("dailyForecast", dailyForecastData)
                intent.putExtra("hourlyForecast", hourlyForecastData)
                startActivity(intent)
                transferToNext()
                finish()
            } else {
                Toast.makeText(this, "날씨 상세정보를 불러오는 데에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        btnProfile.setOnClickListener()
        {
            startActivity(Intent(this, MyPageActivity::class.java))
            transferToNext()
            finish()
        }
        recyclerChat = binding.recyclerChats
        imgbtnChangeLocaion.setOnClickListener()
        {
            var intent = Intent(this, SelectRegionActivity::class.java)
            intent.putExtra("for", "change")
            startActivity(intent)
            transferToNext()
            finish()
        }
    }

    override fun doBack() {
        GeneralConfirmDialog(
            this,
            { logout() },
            "로그아웃 하시겠습니까? \uD83D\uDD13",
            "로그아웃"
        ).show(supportFragmentManager, "generalConfirmDialog")
    }

    fun logout() {
        viewModel.logout()
        viewModel.logoutResponseString.observe(this, {
            if (it.equals("OK")) {
                startActivity(Intent(this, LoginActivity::class.java))
                transferToBack()
                finish()
            } else {
            }
        })
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
            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToNext()
            finish()
        }
    }

    fun getBriefForecast() {
        fun forecastRequest() {
            viewModel.getBriefForecast(defaultRegionId.toInt())
            viewModel.forecastBriefResponse.observe(this, {
                if (it == null) {
                    Log.i("getBriefForecast", "실패")
                } else {
                    if (it is Response<*>) {
                        try {
                            Log.i("결과", "성공")
                            val forecastBriefResponseDto = it.body()!! as ForecastBriefResponseDto
                            briefForecastData = forecastBriefResponseDto.data
                            txtWeatherEmoji.setText(forecastBriefResponseDto.data.weather)
                            txtWeatherDegree.setText(
                                forecastBriefResponseDto.data.temperature.split(
                                    "."
                                )[0] + "˚"
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this, "날씨 단기예보 불러오기에 실패하였습니다.", Toast.LENGTH_SHORT)
                                .show()
                            Log.i("getBriefForecast", "excepted")
                        } finally {
                            txtLocation.text =
                                bigScale + " " + midScale + " " +
                                        if (midScale.equals("전체")) "" else smallScale
                            setupSurveyResult()
                        }
                    } else {
                        if (it is String) {

                        } else if (it is Throwable) {

                        }
                    }
                }
            })
        }
        try {
            forecastRequest()
        } catch (e: Exception) {

        }
    }

    fun getCommentList() {
        viewModel.getCommentList(null, 5)
        viewModel.commentListResponse.observe(this, {
            try {
                setRecyclerChats(it!!.body()!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    fun getDailyForecast() {
        viewModel.getDailyForecast(defaultRegionId.toInt())
        viewModel.dailyForecastResponse.observe(this, {
            if (it == null) {
                dailyForecastData = null
            } else if (it is Response<*>) {
                if (it.isSuccessful) {
                    try {
                        Log.i("결과", "성공")
                        var dailyForecastResponseDto = it.body()!! as DailyForecastResponseDto
                        if (dailyForecastResponseDto.data.dailyForecastList == null)
                            dailyForecastData = null
                        else
                            dailyForecastData = dailyForecastResponseDto.data
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {

                }
            }
        })
    }

    fun getHourlyForecast() {
        viewModel.getHourlyForecast(defaultRegionId.toInt())
        viewModel.hourlyForecastResponse.observe(this, {
            if (it == null) {
                hourlyForecastData = null
            } else if (it is Response<*>) {
                if (it.isSuccessful) {
                    try {
                        Log.i("결과", "성공")
                        var hourlyForecastResponseDto = it.body()!! as HourlyForecastResponseDto
                        if (hourlyForecastResponseDto.data.hourlyForecastList == null)
                            hourlyForecastData = null
                        else
                            hourlyForecastData = hourlyForecastResponseDto.data
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {

                }
            }
        })
    }

    fun setRecyclerChats(responseDto: CommentListResponseDto) {
        recyclerChatAdapter =
            RecyclerChatsAdapter(this, responseDto.data.commentList, false)
        recyclerChat.adapter = recyclerChatAdapter
        recyclerChat.layoutManager = LinearLayoutManager(this)
    }

    fun getUserInfo() {
        fun onSuccessful(response: Response<MemberInfoResponseDto>) {
            try {
                Log.i("결과", "성공")
                val memberInfoResponseDto = response.body()!!
                var memberInfo = memberInfoResponseDto.data
                txtNickName.setText(memberInfo.nickname + "님!")
                txtEmoji.setText(memberInfo.emoji)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun onFail() {
            Log.i("getUserInfo", "불러오기 실패")
        }

        try {
            viewModel.getUserInfo(misoToken)
            viewModel.memberInfoResponse.observe(this, {
                if (it == null) {
                    Log.i("getUserInfo", "에러 발생")
                } else {
                    if (it!!.isSuccessful) {
                        onSuccessful(it)
                    } else {
                        onFail()
                    }

                }
            }
            )
        } catch (e: Exception) {

        }
    }

    fun setupSurveyResult() {
        viewModel.getSurveyResult(getBigShortScale(bigScale))
        viewModel.surveyResultResponse.observe(this, Observer {
            if (it == null) {

            } else {
                if (it.isSuccessful) {
                    try {
                        val todaySurveyResultDto =
                            it.body()!!.data.responseList.first { it.surveyId == 2 }
                        if (todaySurveyResultDto.keyList[0] == null) {
                            imgIconCheckFirst.visibility = View.GONE
                            txtEmptyChart.visibility = View.VISIBLE
                        } else {
                            txtFirstAnswer.text = todaySurveyResultDto.keyList[0].toString()
                            txtFirstRatio.text = todaySurveyResultDto.valueList[0].toString() + "%"
                            if (txtFirstRatio.text.equals("")) {
                                imgIconCheckFirst.visibility = View.GONE
                                txtEmptyChart.visibility = View.VISIBLE
                            } else
                                imgIconCheckFirst.visibility = View.VISIBLE

                            txtFirstRatio.text = todaySurveyResultDto.valueList[0].toString() + "%"
                            firstProgressLayout.visibility = View.VISIBLE

                            if (todaySurveyResultDto.keyList[1] == null)
                                return@Observer
                            txtSecondAnswer.text = todaySurveyResultDto.keyList[1].toString()
                            txtSecondRatio.text = todaySurveyResultDto.valueList[1].toString() + "%"
                            secondProgressLayout.visibility = View.VISIBLE

                            if (todaySurveyResultDto.keyList[2] == null)
                                return@Observer
                            txtThirdAnswer.text = todaySurveyResultDto.keyList[2].toString()
                            txtThirdRatio.text = todaySurveyResultDto.valueList[2].toString() + "%"
                            thirdProgressLayout.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        txtEmptyChart.visibility = View.VISIBLE
                        e.printStackTrace()
                    }
                } else {
                    txtEmptyChart.visibility = View.VISIBLE
                }
            }
        })
    }
}