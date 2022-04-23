package com.miso.misoweather.Acitivity.selectRegion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository2
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SelectRegionActivity : MisoActivity() {
    private lateinit var binding: ActivitySelectRegionBinding
    private lateinit var gridAdapter: RecyclerRegionsAdapter
    private lateinit var grid_region: RecyclerView
    private lateinit var list_towns: RecyclerView
    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button
    private lateinit var aPurpose: String

    @Inject
    lateinit var repository: MisoRepository2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setRecyclerRegions()
    }

    private fun initializeViews() {
        aPurpose = intent.getStringExtra("for") ?: ""
        grid_region = binding.gridRegions
        list_towns = binding.recyclerTowns
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener() {
            doBack()
        }
        btn_next.setOnClickListener() {
            try {
                if (gridAdapter.selectedIndex == -1) Toast.makeText(
                    this,
                    "지역을 선택해 주세요",
                    Toast.LENGTH_SHORT
                ).show()
                else {
                    val intent = Intent(this, SelectTownActivity::class.java)
                    val bigScaleRegion = gridAdapter.getSelectedItemShortName()
                    intent.apply {
                        putExtra("for", aPurpose)
                        putExtra("region", bigScaleRegion)
                    }
                    startActivity(intent)
                    repository.dataStoreManager.savePreference(
                        DataStoreManager.BIGSCALE_REGION, bigScaleRegion
                    )
                    transferToNext()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun doBack() {
        if (aPurpose.equals("change")) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            transferToBack()
            finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            transferToBack()
            finish()
        }
    }

    private fun setRecyclerRegions() {
        gridAdapter = RecyclerRegionsAdapter(this@SelectRegionActivity, getRegionItems())
        grid_region.apply {
            adapter = gridAdapter
            layoutManager = GridLayoutManager(this@SelectRegionActivity, 4)
            addItemDecoration(VerticalSpaceItemDecoration(30))
        }
    }

    private fun getRegionItems(): ArrayList<RegionItem> {
        val regions = resources.getStringArray(R.array.regions)
        val regions_full = resources.getStringArray(R.array.regions_full)
        val regionItems: ArrayList<RegionItem> = ArrayList()
        for (i: Int in regions.indices) {
            val item = RegionItem().apply {
                shortName = regions[i]
                longName = regions_full[i]
            }
            regionItems.add(item)
        }

        return regionItems
    }
}