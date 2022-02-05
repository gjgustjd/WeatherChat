package com.miso.misoweather.Acitivity.mypage

import android.os.Bundle
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityMypageBinding

class MyPageActivity : MisoActivity() {
    lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}