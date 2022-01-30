package com.miso.misoweather.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.databinding.ActivityWeatherMainBinding
import com.miso.misoweather.login.LoginActivity

class HomeActivity :MisoActivity(){
    lateinit var binding:ActivityWeatherMainBinding
    lateinit var btnBack: ImageButton
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
    }
    fun initializeViews()
    {
        btnBack = binding.btnBack
        btnBack.setOnClickListener()
        {
            startActivity(Intent(this,LoginActivity::class.java))
            transferToBack()
            finish()
        }
    }

}