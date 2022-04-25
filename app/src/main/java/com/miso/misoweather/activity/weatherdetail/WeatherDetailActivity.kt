package com.miso.misoweather.activity.weatherdetail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.miso.misoweather.R
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityWeatherMainBinding
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.activity.selectAnswer.SelectSurveyAnswerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class WeatherDetailActivity : MisoActivity() {
    private val viewModel: WeatherDetailViewModel by viewModels()
    private lateinit var binding: ActivityWeatherMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather_main)
        binding.activity = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupWeatherInfo()
    }

    private fun setupWeatherInfo() {
        setupBriefForecastData()
        setupDailyForecastData()
        setupHourlyForecastData()
        setupCurrentAir()
    }

    private fun setupBriefForecastData() {
        lifecycleScope.launch {
            viewModel.getBriefForecast {
                if (!it.isSuccessful) {
                    Log.e("forecastBriefResponse", it.errorBody()!!.source().toString())
                }
            }
        }
    }

    private fun setupDailyForecastData() {
        lifecycleScope.launch {
            viewModel.getDailyForecast {
                if (!it.isSuccessful) {
                    Log.e("setupDailyForecastData", it.errorBody()!!.source().toString())
                }
            }
        }
    }

    private fun setupHourlyForecastData() {
        lifecycleScope.launch {
            viewModel.getHourlyForecast {
                if (!it.isSuccessful) {
                    Log.e("setupHourlyForecastData", it.errorBody()!!.source().toString())
                }
            }
        }
    }

    private fun setupCurrentAir() {
        lifecycleScope.launch {
            viewModel.getCurrentAir {
                if (!it.isSuccessful) {
                    Log.e("currentAirData", it.errorBody()!!.source().toString())
                }
            }
        }
    }

    override fun doBack() {
        startActivity(Intent(this, HomeActivity::class.java))
        transferToBack()
        finish()
    }

    fun goToChatMainActivity() {
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
}