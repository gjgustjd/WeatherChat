package com.miso.misoweather.Acitivity.updateRegion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.Acitivity.selectRegion.RecyclerRegionsAdapter
import com.miso.misoweather.Acitivity.selectRegion.RegionItem
import com.miso.misoweather.databinding.ActivityUpdateRegionBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class UpdateRegionActivity : MisoActivity() {
    private val viewModel: UpdateRegionViewModel by viewModels()
    private lateinit var binding: ActivityUpdateRegionBinding
    private lateinit var gridAdapter: RecyclerRegionsAdapter
    private lateinit var grid_region: RecyclerView
    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button
    private lateinit var currentRegion: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setRecyclerRegions()
    }
    private fun initializeViews() {
        currentRegion =
            intent.getStringExtra("region") ?: viewModel.bigScaleRegion
        grid_region = binding.gridRegions
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener() {
            doBack()
        }
        btn_next.setOnClickListener()
        {
            try {
                val bigScaleRegion = gridAdapter.getSelectedItemShortName()
                viewModel.updateSurveyRegion(bigScaleRegion)
                startActivity(Intent(this, ChatMainActivity::class.java))
                transferToBack()
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun doBack() {
        startActivity(Intent(this, ChatMainActivity::class.java))
        transferToBack()
        finish()
    }

    private fun setRecyclerRegions() {
        val regionItems = getRegionItems()
        val regionList = resources.getStringArray(R.array.regions)
        val index = regionList.indexOf(currentRegion)
        val context = this@UpdateRegionActivity
        gridAdapter = RecyclerRegionsAdapter(context, regionItems, index)
        grid_region.apply {
            adapter = gridAdapter
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(VerticalSpaceItemDecoration(30))
        }

    }

    private fun getRegionItems(): ArrayList<RegionItem> {
        val regions = resources.getStringArray(R.array.regions)
        val regions_full = resources.getStringArray(R.array.regions_full)
        val regionItems = arrayListOf<RegionItem>()
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