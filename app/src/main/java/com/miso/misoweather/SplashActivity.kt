package com.miso.misoweather

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.databinding.ActivitySplashBinding
import com.miso.misoweather.login.LoginActivity
import com.miso.misoweather.selectRegion.SelectRegionActivity
import com.miso.misoweather.selectRegion.WeatherMainActivity

class SplashActivity :AppCompatActivity(){
    lateinit var binding: ActivitySplashBinding
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = getSharedPreferences("access_token", Context.MODE_PRIVATE)
        moveToLogin()
    }

    fun moveToLogin()
    {
       Handler().postDelayed({
          checkKakaoTokenAndLogin()
       },5000)
    }

    fun checkKakaoTokenAndLogin(){
        if(prefs.getString("accesToken","").equals(""))
        {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        else
        {
           if(prefs.getString("misoToken","").equals(""))
               startActivity(Intent(this, SelectRegionActivity::class.java))
            else
               startActivity(Intent(this, WeatherMainActivity::class.java))
        }
    }
}