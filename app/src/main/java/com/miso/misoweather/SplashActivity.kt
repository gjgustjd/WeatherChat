package com.miso.misoweather

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.miso.misoweather.databinding.ActivitySplashBinding
import com.miso.misoweather.login.LoginActivity

class SplashActivity :AppCompatActivity(){
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moveToLogin()
    }

    fun moveToLogin()
    {
       Handler().postDelayed({
          startActivity(Intent(this, LoginActivity::class.java))
       },5000)
    }

}