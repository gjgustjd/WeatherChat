package com.miso.misoweather.Acitivity.home

import android.annotation.SuppressLint
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
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefData
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.Forecast.CurrentAir.CurrentAirData
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastData
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastData
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
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
    lateinit var imgbtnChangeLocation: ImageButton
    lateinit var firstProgressLayout: ConstraintLayout
    lateinit var secondProgressLayout: ConstraintLayout
    lateinit var thirdProgressLayout: ConstraintLayout
    lateinit var chartLayout: ConstraintLayout
    lateinit var txtEmptyChart: TextView

    @Inject
    lateinit var  viewModel: HomeViewModel

    lateinit var isSurveyed: String
    lateinit var lastSurveyedDate: String
    lateinit var defaultRegionId: String
    lateinit var misoToken: String
    lateinit var bigScale: String
    lateinit var midScale: String
    lateinit var smallScale: String
    var currentAirData: CurrentAirData? = null
    var hourlyForecastData: HourlyForecastData? = null
    var dailyForecastData: DailyForecastData? = null
    var briefForecastData: ForecastBriefData? = null

    var isAllInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        initializeProperties()
    }


    private fun setupData() {
        getUserInfo()
        getCommentList()
        getBriefForecast()
        setupSurveyResult()

        txtLocation.text = bigScale + " " + midScale + " " + smallScale
    }

    private fun initializeProperties() {
        fun checkInitializedAll() {
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
            checkInitializedAll()
        })
        viewModel.lastSurveyedDate.observe(this, {
            lastSurveyedDate = it!!
            checkInitializedAll()
        })
        viewModel.defaultRegionId.observe(this, {
            defaultRegionId = it!!
            checkInitializedAll()
        })
        viewModel.misoToken.observe(this, {
            misoToken = it!!
            checkInitializedAll()
        })
        viewModel.bigScale.observe(this, {
            bigScale = it!!
            checkInitializedAll()
        })
        viewModel.midScale.observe(this, {
            midScale = it!!
            checkInitializedAll()
        })
        viewModel.smallScale.observe(this, {
            smallScale = it!!
            checkInitializedAll()
        })

        Log.i("initializeProperties", "Launched")
    }

    private fun isAllForecastDataIsNotNull(): Boolean {
        return dailyForecastData != null &&
                briefForecastData != null &&
                hourlyForecastData != null &&
                currentAirData != null
    }

    fun initializeViews() {
        fun initWeatherLayout() {
            try {
                weatherLayout = binding.weatherLayout
                weatherLayout.setOnClickListener()
                {
                    val intent = Intent(this, WeatherDetailActivity::class.java)
                    startActivity(intent)
                    transferToNext()
                    finish()
                }
            } catch (e: Exception) {
                Log.e("initWeatherLayout", e.stackTraceToString())
            }
        }

        fun initBtnGoToSurvey() {
            try {
                btngoToSurvey = binding.imgbtnSurvey
                btngoToSurvey.setOnClickListener()
                {
                    goToChatMainActivity()
                }
            } catch (e: Exception) {
                Log.e("initBtnGoToSurvey", e.stackTraceToString())
            }
        }

        fun initChartLayout() {
            try {
                chartLayout = binding.chartLayout
                chartLayout.setOnClickListener()
                {
                    goToChatMainActivity()
                }
            } catch (e: Exception) {
                Log.e("initChartLayout", e.stackTraceToString())
            }
        }

        fun initBtnProfile() {
            try {
                btnProfile = binding.imgbtnProfile
                btnProfile.setOnClickListener()
                {
                    startActivity(Intent(this, MyPageActivity::class.java))
                    transferToNext()
                    finish()
                }
            } catch (e: Exception) {
                Log.e("initBtnProfile", e.stackTraceToString())
            }
        }

        @SuppressLint("LongLogTag")
        fun initImgBtnChangeLocation() {
            try {
                imgbtnChangeLocation = binding.imgbtnChangeLocation
                imgbtnChangeLocation.setOnClickListener()
                {
                    val intent = Intent(this, SelectRegionActivity::class.java)
                    intent.putExtra("for", "change")
                    startActivity(intent)
                    transferToNext()
                    finish()
                }
            } catch (e: Exception) {
                Log.e("initImgBtnChangeLctn", e.stackTraceToString())
            }
        }
        txtNickName = binding.txtNickname
        txtEmoji = binding.txtEmoji
        txtLocation = binding.txtLocation
        txtWeatherDegree = binding.txtDegree
        txtWeatherEmoji = binding.txtWeatherEmoji
        btnShowWeatherDetail = binding.imgbtnShowWeather
        txtFirstAnswer = binding.txtAnswerFirst
        txtSecondAnswer = binding.txtAnswerSecond
        txtThirdAnswer = binding.txtAnswerThird
        txtFirstRatio = binding.txtRatioFirst
        txtSecondRatio = binding.txtRatioSecond
        txtThirdRatio = binding.txtRatioThird
        imgIconCheckFirst = binding.imgIconFirst
        firstProgressLayout = binding.itemFirstLayout
        secondProgressLayout = binding.itemSecondLayout
        thirdProgressLayout = binding.itemThirdLayout
        txtEmptyChart = binding.txtEmptyChart
        recyclerChat = binding.recyclerChats

        initWeatherLayout()
        initChartLayout()
        initBtnGoToSurvey()
        initBtnProfile()
        initImgBtnChangeLocation()
    }


    override fun doBack() {
        GeneralConfirmDialog(
            this,
            { logout() },
            "로그아웃 하시겠습니까? \uD83D\uDD13",
            "로그아웃"
        ).show(supportFragmentManager, "generalConfirmDialog")
    }

    private fun logout() {
        viewModel.logout()
        viewModel.logoutResponseString.observe(this, {
            if (!it.equals("OK")) {
                Toast.makeText(this, "카카오 로그아웃에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this, LoginActivity::class.java))
            transferToBack()
            finish()
        })
    }

    private fun goToChatMainActivity() {
        try {
            val intent: Intent

            if (!isSurveyed.equals("true") || !isTodaySurveyed()) {
                intent = Intent(this, SelectSurveyAnswerActivity::class.java)
                intent.putExtra("isFirstSurvey", true)
            } else {
                intent = Intent(this, ChatMainActivity::class.java)
            }

            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToNext()
            finish()
        } catch (e: Exception) {
            Log.e("goToChatMainActivity", e.stackTraceToString())
            Toast.makeText(this, "화면 이동 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isTodaySurveyed(): Boolean {
        try {
            val currentDate =
                ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
            return lastSurveyedDate.equals(currentDate)
        } catch (e: Exception) {
            Log.e("isTodaySurveyed", e.stackTraceToString())
            return true
        }
    }

    private fun getBriefForecast() {
        if (!defaultRegionId.isNullOrBlank()) {
            Log.i("defaultRegionId", defaultRegionId)
            var previousBigScale = bigScale
            viewModel.getBriefForecast(defaultRegionId.toInt())
            viewModel.forecastBriefResponse.observe(this, {
                try {
                    if (it != null) {
                        if (it is Response<*>) {
                            if (it.isSuccessful) {
                                val forecastBriefResponseDto =
                                    it.body()!! as ForecastBriefResponseDto
                                briefForecastData = forecastBriefResponseDto.data
                                txtWeatherEmoji.setText(forecastBriefResponseDto.data.weather)
                                txtWeatherDegree.setText(
                                    CommonUtil.toIntString(forecastBriefResponseDto.data.temperature) + "˚"
                                )
                                txtLocation.text = bigScale + " " + midScale + " " + smallScale

                                if (!previousBigScale.equals(bigScale))
                                    setupSurveyResult()

                                Log.i("결과", "성공")
                            } else
                                throw Exception(it.errorBody()!!.source().toString())

                        } else {
                            if (it is String)
                                throw Exception(it)
                            else if (it is Throwable)
                                throw it
                            else
                                throw Exception("실패")
                        }
                    } else {
                        throw Exception("null")
                    }
                } catch (e: Exception) {
                    if (!e.message.isNullOrBlank())
                        Log.e("getBriefForecast", e.message.toString())

                    Log.e("getBriefForecast", e.stackTraceToString())

                    Toast.makeText(this, "날씨 단기예보 불러오기에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } else {
            Log.e("getBriefForecast", "defaultRegionId is blank")
            Toast.makeText(this, "날씨 단기예보 불러오기에 실패하였습니다.", Toast.LENGTH_SHORT)
                .show()
        }
    }


    fun getCommentList() {
        viewModel.getCommentList(null, 5)
        viewModel.commentListResponse.observe(this, {
            setRecyclerChats(it!!.body()!!)
        })
    }


    fun setRecyclerChats(responseDto: CommentListResponseDto) {
        try {
            recyclerChatAdapter =
                RecyclerChatsAdapter(this, responseDto.data.commentList, false)
            recyclerChat.adapter = recyclerChatAdapter
            recyclerChat.layoutManager = LinearLayoutManager(this)
            Log.i("setRecyclerChats", "성공")
        } catch (e: Exception) {
            Toast.makeText(this, "한줄평 목록을 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun getUserInfo() {
        fun onSuccessful(response: Response<MemberInfoResponseDto>) {
            try {
                val memberInfoResponseDto = response.body()!!
                var memberInfo = memberInfoResponseDto.data
                txtNickName.setText(memberInfo.nickname + "님!")
                txtEmoji.setText(memberInfo.emoji)
                if (!isAllForecastDataIsNotNull())
                    getBriefForecast()

                Log.i("getUserInfo", "성공")
            } catch (e: Exception) {
                Toast.makeText(this, "사용자 정보를 불러오는 중 문제가 발생했습니다.", Toast.LENGTH_SHORT)
                    .show()
                Log.e("getUserInfo", e.stackTraceToString())
            }
        }

        fun onFail(response: Response<MemberInfoResponseDto>) {
            Toast.makeText(this, "사용자 정보를 불러오는 중 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
            Log.e("getUserInfo", response.errorBody()!!.source().toString())
        }

        viewModel.getUserInfo(misoToken)
        viewModel.memberInfoResponse.observe(this, {
            if (it == null) {
                Log.i("getUserInfo", "에러 발생")
            } else {
                if (it!!.isSuccessful) {
                    onSuccessful(it)
                } else {
                    onFail(it)
                }
            }
        }
        )
    }

    fun setupSurveyResult() {
        val answerViews = listOf(txtFirstAnswer, txtSecondAnswer, txtThirdAnswer)
        val ratioViews = listOf(txtFirstRatio, txtSecondRatio, txtThirdRatio)
        val progressLayouts =
            listOf(firstProgressLayout, secondProgressLayout, thirdProgressLayout)

        fun showEmptyChartText() {
            txtFirstRatio.visibility = View.GONE
            imgIconCheckFirst.visibility = View.GONE
            txtEmptyChart.visibility = View.VISIBLE
        }

        fun showChartItem(
            surveyResult: SurveyResult,
            index: Int
        ) {
            answerViews[index].text = surveyResult.keyList[index].toString()
            ratioViews[index].text = surveyResult.valueList[index].toString() + "%"
            progressLayouts[index].visibility = View.VISIBLE
        }

        fun showFirstItem(surveyResultDto: SurveyResult) {
            showChartItem(surveyResultDto, 0)

            if (txtFirstRatio.text.equals("")) {
                showEmptyChartText()
            } else
                imgIconCheckFirst.visibility = View.VISIBLE
        }

        viewModel.getSurveyResult()
        viewModel.surveyResultResponse.observe(this, Observer {
            try {
                if (it == null) {
                    throw Exception("null")
                } else {
                    if (it.isSuccessful) {
                        val todaySurveyResultDto =
                            it.body()!!.data.responseList.first { it.surveyId == 2 }

                        todaySurveyResultDto.keyList.forEachIndexed { index, it ->
                            try {
                                if (index == 0) {
                                    if (it != null)
                                        showFirstItem(todaySurveyResultDto)
                                    else
                                        showEmptyChartText()
                                } else
                                    if (it != null)
                                        showChartItem(todaySurveyResultDto, index)
                                    else
                                        return@forEachIndexed
                            } catch (e: IndexOutOfBoundsException) {
                                Log.e("setupSurveyResult", e.stackTraceToString())
                                return@forEachIndexed
                            }
                        }
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                }
            } catch (e: Exception) {
                showEmptyChartText()

                if (!e.message.toString().equals(""))
                    Log.e("setupSurveyResult", e.message.toString())
                else
                    Log.e("setupSurveyResult", e.stackTraceToString())

                Toast.makeText(this, "차트를 불러오는데에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}