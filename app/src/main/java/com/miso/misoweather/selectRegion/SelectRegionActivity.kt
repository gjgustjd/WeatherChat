package com.miso.misoweather.selectRegion

import android.os.Bundle
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.databinding.ActivitySelectRegionBinding

class SelectRegionActivity :AppCompatActivity(){
    lateinit var binding:ActivitySelectRegionBinding
    lateinit var grid_region:RecyclerView
    lateinit var list_towns:RecyclerView
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
    }
    fun setRecyclerRegions()
    {
        var regions= resources.getStringArray(R.array.regions)
        var adapter: RecyclerRegionsAdapter = RecyclerRegionsAdapter(this@SelectRegionActivity,regions)
        grid_region.adapter=adapter
        grid_region.layoutManager = GridLayoutManager(this,4)
        val spaceDecoration = VerticalSpaceItemDecoration(40)
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