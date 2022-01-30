package com.miso.misoweather

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySplashBinding
import com.miso.misoweather.home.HomeActivity
import com.miso.misoweather.login.LoginActivity
import com.miso.misoweather.selectRegion.SelectRegionActivity

class SplashActivity : MisoActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moveToLogin()
    }

    fun moveToLogin() {
        Handler().postDelayed({
            checkKakaoTokenAndLogin()
            finish()
        }, 2000)
    }

    fun checkKakaoTokenAndLogin() {
        if (getPreference("accessToken").equals("")) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            var intent: Intent
            if (getPreference("misoToken").equals(""))
                intent = Intent(this, SelectRegionActivity::class.java)
            else
                intent = Intent(this, HomeActivity::class.java)


            startActivity(intent)
        }
    }
}