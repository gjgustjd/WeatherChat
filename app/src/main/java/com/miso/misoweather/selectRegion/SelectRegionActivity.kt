package com.miso.misoweather.selectRegion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.login.LoginActivity

class SelectRegionActivity :AppCompatActivity(){
    lateinit var binding:ActivitySelectRegionBinding
    lateinit var grid_region:RecyclerView
    lateinit var list_towns:RecyclerView
    lateinit var btn_back:ImageButton
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setRecyclerRegions()
        setRecyclerTowns()

    }
    fun initializeViews()
    {
        grid_region = binding.gridRegions
        list_towns = binding.recyclerTowns
        btn_back = binding.imgbtnBack
        btn_back.setOnClickListener(){
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }
    fun setRecyclerRegions()
    {
        var regions= resources.getStringArray(R.array.regions)
        var adapter: RecyclerRegionsAdapter = RecyclerRegionsAdapter(this@SelectRegionActivity,regions)
        grid_region.adapter=adapter
        grid_region.layoutManager = GridLayoutManager(this,4)
        val spaceDecoration = VerticalSpaceItemDecoration(30)
        grid_region.addItemDecoration(spaceDecoration)
    }
    fun setRecyclerTowns()
    {
        var towns= resources.getStringArray(R.array.towns)
        var adapter: RecyclerTownsAdapter = RecyclerTownsAdapter(this@SelectRegionActivity,towns)
        list_towns.adapter=adapter
        list_towns.layoutManager = LinearLayoutManager(this)
        val spaceDecoration = DividerItemDecoration(applicationContext,VERTICAL)
        list_towns.addItemDecoration(spaceDecoration)
    }

}