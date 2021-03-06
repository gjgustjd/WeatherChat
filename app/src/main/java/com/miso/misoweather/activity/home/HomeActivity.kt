package com.miso.misoweather.activity.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.activity.login.LoginActivity
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityHomeBinding
import com.miso.misoweather.activity.mypage.MyPageActivity
import com.miso.misoweather.activity.selectAnswer.SelectSurveyAnswerActivity
import com.miso.misoweather.activity.selectRegion.SelectRegionActivity
import com.miso.misoweather.dialog.GeneralConfirmDialog
import com.miso.misoweather.R
import com.miso.misoweather.activity.weatherdetail.WeatherDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class HomeActivity : MisoActivity() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        initializeViews()
        setupData()
    }

    private fun setupData() {
        getUserInfo()
        getCommentList()
        setupSurveyResult()
    }

    private fun initializeViews() {
        binding.viewModel = viewModel
        binding.activity = this
        binding.lifecycleOwner = this
    }

    fun goToWeatherDetailActivity() {
        val intent = Intent(this, WeatherDetailActivity::class.java)
        intent.putExtra(
            "isTodaySurveyed",
            (viewModel.isSurveyed.value.equals("surveyed") || isTodaySurveyed())
        )
        startActivity(intent)
        transferToNext()
        finish()
    }

    fun goToSelectRegionActivity() {
        val intent = Intent(this, SelectRegionActivity::class.java)
        intent.putExtra("for", "change")
        startActivity(intent)
        transferToNext()
        finish()
    }

    fun goToMyPageActivity() {
        startActivity(Intent(this, MyPageActivity::class.java))
        transferToNext()
        finish()
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

    fun goToChatMainActivity() {
        try {
            val intent: Intent

            if (isTodaySurveyed() || viewModel.isSurveyed.value.equals("surveyed")) {
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
        val previousBigScale = viewModel.bigScale.value
        lifecycleScope.launch {
            viewModel.getBriefForecast()
            {
                try {
                    if (it.isSuccessful) {
                        if (!previousBigScale.equals(viewModel.bigScale.value))
                            setupSurveyResult()

                        Log.i("결과", "성공")
                    } else
                        throw Exception(it.errorBody()!!.source().toString())
                } catch (e: Exception) {
                    Log.e("getBriefForecast", e.stackTraceToString())
                    Toast.makeText(
                        this@HomeActivity,
                        "날씨 단기예보 불러오기에 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getCommentList() {
        lifecycleScope.launch {
            viewModel.getCommentList(null, 5)
        }
    }
    private fun getUserInfo() {
        lifecycleScope.launch {
            viewModel.getUserInfo()
            {
                if (it.isSuccessful) {
                    Log.i("getUserInfo", "성공")
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "사용자 정보를 불러오는 중 문제가 발생했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("getUserInfo", it.errorBody()!!.source().toString())
                }

                getBriefForecast()
            }
        }
    }

    private fun setupSurveyResult() {
        lifecycleScope.launch {
            viewModel.getSurveyResult()
            {
                if (!it.isSuccessful) {
                    Log.e("setupSurveyResult", it.errorBody()!!.source().toString())
                    Toast.makeText(this@HomeActivity, "차트를 불러오는데에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}