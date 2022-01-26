package com.miso.misoweather.selectRegion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout.VERTICAL
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.login.LoginActivity

class SelectRegionActivity : MisoActivity() {
    lateinit var binding:ActivitySelectRegionBinding
    lateinit var gridAdapter: RecyclerRegionsAdapter
    lateinit var grid_region:RecyclerView
    lateinit var list_towns:RecyclerView
    lateinit var btn_back:ImageButton
    lateinit var btn_next: Button
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setRecyclerRegions()
    }
    fun initializeViews()
    {
        grid_region = binding.gridRegions
        list_towns = binding.recyclerTowns
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener(){
            startActivity(Intent(this,LoginActivity::class.java))
            transferToBack()
            finish()
        }
       btn_next.setOnClickListener()
       {
           var intent:Intent = Intent(this,SelectTownActivity::class.java)
           intent.putExtra("region",gridAdapter.getSelectedItemName())
           startActivity(intent)
           transferToNext()
           finish()
       }
    }
    fun setRecyclerRegions()
    {
        var regions= resources.getStringArray(R.array.regions)
        gridAdapter = RecyclerRegionsAdapter(this@SelectRegionActivity,regions)
        grid_region.adapter=gridAdapter
        grid_region.layoutManager = GridLayoutManager(this,4)
        val spaceDecoration = VerticalSpaceItemDecoration(30)
        grid_region.addItemDecoration(spaceDecoration)
    }
}