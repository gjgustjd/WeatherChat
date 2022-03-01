package com.miso.misoweather.Acitivity.selectRegion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import java.lang.Exception

class SelectRegionActivity : MisoActivity() {
    lateinit var binding: ActivitySelectRegionBinding
    lateinit var gridAdapter: RecyclerRegionsAdapter
    lateinit var grid_region: RecyclerView
    lateinit var list_towns: RecyclerView
    lateinit var btn_back: ImageButton
    lateinit var btn_next: Button
    lateinit var aPurpose: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setRecyclerRegions()
    }

    fun initializeViews() {
        aPurpose = intent.getStringExtra("for") ?: ""
        grid_region = binding.gridRegions
        list_towns = binding.recyclerTowns
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener() {
            if (aPurpose.equals("change")) {

                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                transferToBack()
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                transferToBack()
                finish()
            }
        }
        btn_next.setOnClickListener()
        {
            try {
                if (gridAdapter.selectedIndex == -1)
                    Toast.makeText(this, "지역을 선택해 주세요", Toast.LENGTH_SHORT).show()
                else {
                    var intent = Intent(this, SelectTownActivity::class.java)
                    var bigScaleRegion = gridAdapter.getSelectedItemShortName()
                    intent.putExtra("for", aPurpose)
                    intent.putExtra("region", bigScaleRegion)
                    startActivity(intent)
                    addPreferencePair("BigScaleRegion", bigScaleRegion)
                    transferToNext()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                savePreferences()
            }
        }
    }

    fun setRecyclerRegions() {
        gridAdapter = RecyclerRegionsAdapter(this@SelectRegionActivity, getRegionItems())
        grid_region.adapter = gridAdapter
        grid_region.layoutManager = GridLayoutManager(this, 4)
        val spaceDecoration = VerticalSpaceItemDecoration(30)
        grid_region.addItemDecoration(spaceDecoration)
    }

    fun getRegionItems(): ArrayList<RegionItem> {
        var regions = resources.getStringArray(R.array.regions)
        var regions_full = resources.getStringArray(R.array.regions_full)
        var regionItems: ArrayList<RegionItem> = ArrayList<RegionItem>()
        for (i: Int in 0..regions.size - 1) {
            var item: RegionItem = RegionItem()
            item.shortName = regions.get(i)
            item.longName = regions_full.get(i)
            regionItems.add(item)
        }

        return regionItems
    }
}