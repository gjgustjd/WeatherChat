package com.miso.misoweather

import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.databinding.ActivitySelectRegionBinding

class SelectRegionActivity :AppCompatActivity(){
    lateinit var binding:ActivitySelectRegionBinding
    lateinit var grid_region:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()

    }
    fun initializeViews()
    {
        grid_region = binding.gridRegions
        var regions= resources.getStringArray(R.array.regions)
        var adapter:RecyclerRegionsAdapter = RecyclerRegionsAdapter(this@SelectRegionActivity,regions)
        grid_region.adapter=adapter
        grid_region.layoutManager = GridLayoutManager(this,4)


    }

}