package com.miso.misoweather.selectRegion

import android.graphics.Paint
import android.os.Bundle
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.databinding.ActivitySelectRegionBinding

class SelectNickNameActivity :AppCompatActivity(){
    lateinit var binding:ActivitySelectNicknameBinding
    lateinit var txt_get_new_nick:TextView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        binding = ActivitySelectNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()

    }
    fun initializeViews()
    {
        txt_get_new_nick = binding.txtGetNewNickname
        txt_get_new_nick.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }
}