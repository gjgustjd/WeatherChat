package com.miso.misoweather

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.miso.misoweather.databinding.ActivitySplashBinding

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
          startActivity(Intent(this,MainActivity::class.java))
       },5000)
    }
}