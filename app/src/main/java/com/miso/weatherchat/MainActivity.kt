package com.miso.weatherchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.miso.weatherchat.databinding.ActivityLoginBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}