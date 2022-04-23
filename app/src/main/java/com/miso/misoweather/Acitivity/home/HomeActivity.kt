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
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
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
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class HomeActivity : MisoActivity() {
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: ActivityHomeBinding
    private lateinit var weatherLayout: ConstraintLayout
    private lateinit var txtNickName: TextView
    private lateinit var txtEmoji: TextView
    private lateinit var txtLocation: TextView
    private lateinit var txtWeatherEmoji: TextView
    private lateinit var txtWeatherDegree: TextView
    private lateinit var btnShowWeatherDetail: ImageButton
    private lateinit var btnProfile: ImageButton
    private lateinit var btngoToSurvey: ImageButton
    private lateinit var recyclerChat: RecyclerView
    private lateinit var recyclerChatAdapter: RecyclerChatsAdapter
    private lateinit var txtFirstAnswer: TextView
    private lateinit var txtSecondAnswer: TextView
    private lateinit var txtThirdAnswer: TextView
    private lateinit var txtFirstRatio: TextView
    private lateinit var txtSecondRatio: TextView
    private lateinit var txtThirdRatio: TextView
    private lateinit var imgIconCheckFirst: ImageView
    private lateinit var imgbtnChangeLocation: ImageButton
    private lateinit var firstProgressLayout: ConstraintLayout
    private lateinit var secondProgressLayout: ConstraintLayout
    private lateinit var thirdProgressLayout: ConstraintLayout
    private lateinit var chartLayout: ConstraintLayout
    private lateinit var txtEmptyChart: TextView
    private var briefForecastData: ForecastBriefData? = null
    private lateinit var isSurveyed: String
    private lateinit var defaultRegionId: String
    private lateinit var bigScale: String
    private lateinit var midScale: String
    private lateinit var smallScale: String
    private var isAllInitialized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProperties()
        initializeViews()
    }

    private fun setupData() {
        getUserInfo()
        getCommentList()
        setupSurveyResult()

        txtLocation.text = "${bigScale} ${midScale} ${smallScale}"
    }

    private fun initializeProperties() {
        fun checkInitializedAll() {
            if (!isAllInitialized) {
                if (
                    this::isSurveyed.isInitialized &&
                    this::defaultRegionId.isInitialized &&
                    this::bigScale.isInitialized &&
                    this::midScale.isInitialized &&
                    this::smallScale.isInitialized
                ) {
                    setupData()
                    isAllInitialized = true
                }
            }
        }
        viewModel.isSurveyed.observe(this) {
            isSurveyed = it!!
            checkInitializedAll()
        }
        viewModel.defaultRegionId.observe(this) {
            defaultRegionId = it!!
            checkInitializedAll()
        }
        viewModel.bigScale.observe(this) {
            bigScale = it!!
            checkInitializedAll()
        }
        viewModel.midScale.observe(this) {
            midScale = it!!
            checkInitializedAll()
        }
        viewModel.smallScale.observe(this) {
            smallScale = it!!
            checkInitializedAll()
        }

        Log.i("initializeProperties", "Launched")
    }

    private fun initializeViews() {
        fun initWeatherLayout() {
            try {
                weatherLayout = binding.weatherLayout
                weatherLayout.setOnClickListener()
                {
                    val intent = Intent(this, WeatherDetailActivity::class.java)
                    intent.putExtra(
                        "isTodaySurveyed",
                        (isSurveyed.equals("surveyed") || isTodaySurveyed())
                    )
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
        viewModel.logoutResponseString.observe(this) {
            if (!it.equals("OK")) {
                Toast.makeText(this, "카카오 로그아웃에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this, LoginActivity::class.java))
            transferToBack()
            finish()
        }
    }

    private fun goToChatMainActivity() {
        try {
            val intent: Intent

            if (isTodaySurveyed() || isSurveyed.equals("surveyed")) {
                intent = Intent(this, ChatMainActivity::class.java)
            } else {
                intent = Intent(this, SelectSurveyAnswerActivity::class.java)
                intent.putExtra("isFirstSurvey", true)
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
        return try {
            val currentDate =
                ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
            viewModel.lastSurveyedDate.equals(currentDate)
        } catch (e: Exception) {
            Log.e("isTodaySurveyed", e.stackTraceToString())
            true
        }
    }

    private fun getBriefForecast() {
        if (briefForecastData == null) {
            if (!defaultRegionId.isNullOrBlank()) {
                Log.i("defaultRegionId", defaultRegionId)
                val previousBigScale = bigScale
                lifecycleScope.launch {
                    viewModel.getBriefForecast(defaultRegionId.toInt())
                    {
                        try {
                            if (it.isSuccessful) {
                                val forecastBriefResponseDto =
                                    it.body()!!
                                briefForecastData = forecastBriefResponseDto.data
                                txtWeatherEmoji.setText(forecastBriefResponseDto.data.weather)
                                txtWeatherDegree.setText(
                                    CommonUtil.toIntString(forecastBriefResponseDto.data.temperature) + "˚"
                                )
                                txtLocation.text =
                                    "${bigScale} ${midScale} ${smallScale}"

                                if (!previousBigScale.equals(bigScale))
                                    setupSurveyResult()

                                Log.i("결과", "성공")
                            } else
                                throw Exception(it.errorBody()!!.source().toString())
                        } catch (e: Exception) {
                            if (!e.message.isNullOrBlank())
                                Log.e("getBriefForecast", e.message.toString())

                            Log.e("getBriefForecast", e.stackTraceToString())
                            Log.e(
                                "getBriefForecast",
                                "defaultRegionId:${viewModel.defaultRegionId}"
                            )
                            Toast.makeText(
                                this@HomeActivity,
                                "날씨 단기예보 불러오기에 실패하였습니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            } else {
                Log.e("getBriefForecast", "defaultRegionId is blank")
                Toast.makeText(this, "날씨 단기예보 불러오기에 실패하였습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun getCommentList() {
        lifecycleScope.launch {
            viewModel.getCommentList(null, 5)
            {
                setRecyclerChats(it!!.body()!! as CommentListResponseDto)
            }
        }
    }


    private fun setRecyclerChats(responseDto: CommentListResponseDto) {
        try {
            recyclerChatAdapter =
                RecyclerChatsAdapter(responseDto.data.commentList, false)
            recyclerChat.apply {
                adapter = recyclerChatAdapter
                layoutManager = LinearLayoutManager(this@HomeActivity)
            }
            Log.i("setRecyclerChats", "성공")
        } catch (e: Exception) {
            Toast.makeText(this, "한줄평 목록을 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserInfo() {
        fun onSuccessful(response: Response<MemberInfoResponseDto>) {
            try {
                val memberInfoResponseDto = response.body()!!
                val memberInfo = memberInfoResponseDto.data
                txtNickName.text = "${memberInfo.regionName}의 ${memberInfo.nickname}님!"
                txtEmoji.text = memberInfo.emoji

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

        lifecycleScope.launch {
            viewModel.getUserInfo()
            {
                if (it.isSuccessful) {
                    onSuccessful(it)
                } else {
                    onFail(it)
                }
                getBriefForecast()
            }
        }
    }

    private fun setupSurveyResult() {
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

        lifecycleScope.launch {
            viewModel.getSurveyResult()
            {
                try {
                    if (it.isSuccessful) {
                        val todaySurveyResultDto =
                            it.body()!!.data.responseList.first { it.surveyId == 2 }

                        todaySurveyResultDto.keyList.forEachIndexed { index, it ->
                            try {
                                if (index >= answerViews.size)
                                    return@forEachIndexed
                                else {
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
                                }
                            } catch (e: IndexOutOfBoundsException) {
                                Log.e("setupSurveyResult", e.stackTraceToString())
                                return@forEachIndexed
                            }
                        }
                    } else {
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } catch (e: Exception) {
                    showEmptyChartText()

                    if (!e.message.toString().equals(""))
                        Log.e("setupSurveyResult", e.message.toString())
                    else
                        Log.e("setupSurveyResult", e.stackTraceToString())

                    Toast.makeText(this@HomeActivity, "차트를 불러오는데에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}