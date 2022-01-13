package com.miso.misoweather

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Adapter
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding

class SelectRegionActivity :AppCompatActivity(){
    lateinit var binding:ActivitySelectRegionBinding
    lateinit var grid_region:GridView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    fun initializeViews()
    {
        grid_region = binding.gridRegions
        var regions:ArrayList<String> = ArrayList()
        regions.add("서울")
        regions.add("경기")
        var adapter:GridRegionsAdapter = GridRegionsAdapter(this@SelectRegionActivity,regions)
        grid_region.adapter=adapter

    }

}