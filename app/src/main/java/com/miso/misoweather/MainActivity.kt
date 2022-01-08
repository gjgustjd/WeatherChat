package com.miso.misoweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.miso.misoweather.databinding.ActivityLoginBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}