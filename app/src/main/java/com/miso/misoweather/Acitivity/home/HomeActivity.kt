package com.miso.misoweather.Acitivity.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.sdk.user.UserApiClient
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
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception
import java.time.LocalDateTime
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        getUserInfo()
        getBriefForecast()
        getCommentList()
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

            startActivity(Intent(this, WeatherDetailActivity::class.java))
            transferToNext()
            finish()
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
            View.OnClickListener {
                logout()
            },
            "로그아웃 하시겠습니까? \uD83D\uDD13",
            "로그아웃"
        ).show(supportFragmentManager, "generalConfirmDialog")
    }

    fun logout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("kakaoLogout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            } else {
                Log.i("kakaoLogout", "로그아웃 성공. SDK에서 토큰 삭제됨")
                removePreference("accessToken", "socialId", "socialType", "misoToken")
                savePreferences()
                startActivity(Intent(this, LoginActivity::class.java))
                transferToBack()
                finish()
            }
        }
    }

    fun goToChatMainActivity() {
        var isSurveyed = getPreference("isSurveyed")
        var lastSurveyedDate = getPreference("LastSurveyedDate") ?: ""
        var currentDate =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
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
        var repeatCount = 0
        fun forecastRequest() {
            fun repeatRequest() {
                if (repeatCount < 3) {
                    repeatCount++
                    forecastRequest()
                    Log.i("getBriefForecast", "repeated")
                }
            }

            viewModel.getBriefForecast(getPreference("defaultRegionId")!!.toInt())
            viewModel.forecastBriefResponse.observe(this, {
                if (it == null) {
                    Log.i("getBriefForecast", "실패")
                    repeatRequest()
                } else {
                    if (it.isSuccessful) {
                        try {
                            Log.i("결과", "성공")
                            val forecastBriefResponseDto = it.body()!!
                            var forecast = forecastBriefResponseDto.data.forecast
                            var region = forecastBriefResponseDto.data.region
                            addPreferencePair("bigScale", region.bigScale)
                            addPreferencePair(
                                "midScale",
                                if (region.midScale.equals("선택 안 함")) "전체" else region.midScale
                            )
                            addPreferencePair(
                                "smallScale",
                                if (region.smallScale.equals("선택 안 함")) "전체" else region.smallScale
                            )
                            savePreferences()
                            txtLocation.text =
                                region.bigScale + " " + getPreference("midScale") + " " +
                                        if (getPreference("midScale").equals("전체")) "" else getPreference(
                                            "smallScale"
                                        )
                            txtWeatherEmoji.setText(forecast.sky)
                            txtWeatherDegree.setText(forecast.temperature + "˚")
                            setupSurveyResult()
                        } catch (e: Exception) {
                            repeatRequest()
                            e.printStackTrace()
                            Log.i("getBriefForecast", "excepted")
                        }
                    } else {
                        repeatRequest()
                    }
                }
            })
        }
        try {
            forecastRequest()
        } catch (e: Exception)
        {

        }
    }

    fun getCommentList() {
        viewModel.getCommentList(null, 5)
        viewModel.commentListResponse.observe(this, {
            try {
                Log.i("결과", "성공")

                setRecyclerChats(it!!.body()!!)
            } catch (e: Exception) {
                e.printStackTrace()
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
                addPreferencePair(
                    "defaultRegionId",
                    memberInfoResponseDto.data.regionId.toString()
                )
                addPreferencePair("emoji", memberInfo.emoji)
                addPreferencePair("nickname", memberInfo.nickname)
                savePreferences()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun onFail() {
            Log.i("getUserInfo", "불러오기 실패")
        }

        viewModel.getUserInfo(getPreference("misoToken")!!)
        viewModel.memberInfoResponse.observe(this, Observer {
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
    }

    fun setupSurveyResult() {
        viewModel.getSurveyResult(getBigShortScale(getPreference("bigScale")!!))
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