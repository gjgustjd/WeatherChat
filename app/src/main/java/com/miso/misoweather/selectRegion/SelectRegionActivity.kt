package com.miso.misoweather.selectRegion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.login.LoginActivity
import com.miso.misoweather.selectTown.SelectTownActivity

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
           var intent:Intent = Intent(this, SelectTownActivity::class.java)
           intent.putExtra("region",gridAdapter.getSelectedItemLongName())
           startActivity(intent)
           transferToNext()
           finish()
       }
    }
    fun setRecyclerRegions()
    {
        gridAdapter = RecyclerRegionsAdapter(this@SelectRegionActivity,getRegionItems())
        grid_region.adapter=gridAdapter
        grid_region.layoutManager = GridLayoutManager(this,4)
        val spaceDecoration = VerticalSpaceItemDecoration(30)
        grid_region.addItemDecoration(spaceDecoration)
    }
    fun getRegionItems():ArrayList<RegionItem>
    {
        var regions= resources.getStringArray(R.array.regions)
        var regions_full= resources.getStringArray(R.array.regions_full)
        var regionItems:ArrayList<RegionItem> = ArrayList<RegionItem>()
        for(i:Int in 0..regions.size-1)
        {
            var item:RegionItem = RegionItem()
            item.shortName=regions.get(i)
            item.longName=regions_full.get(i)
            regionItems.add(item)
        }

        return regionItems
    }
}