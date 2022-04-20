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
import javax.inject.Inject

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
    private lateinit var surveyRegion: String
    private lateinit var bigScaleRegion: String
    private var isAllInitialized = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProperties()
    }

    private fun initializeProperties() {
        fun checkinitializedAll() {
            if (!isAllInitialized) {
                if (
                    this::bigScaleRegion.isInitialized &&
                    this::surveyRegion.isInitialized
                ) {
                    initializeViews()
                    setRecyclerRegions()
                    isAllInitialized = true
                }
            }
        }
        viewModel.updateProperties()
        viewModel.surveyRegion.observe(this) {
            surveyRegion = it!!
            checkinitializedAll()
        }
        viewModel.bigScaleRegion.observe(this) {
            bigScaleRegion = it!!
            checkinitializedAll()
        }
    }

    private fun initializeViews() {
        currentRegion =
            intent.getStringExtra("region") ?: bigScaleRegion
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
        gridAdapter = RecyclerRegionsAdapter(this@UpdateRegionActivity, regionItems, index)
        grid_region.adapter = gridAdapter
        grid_region.layoutManager = GridLayoutManager(this, 4)
        val spaceDecoration = VerticalSpaceItemDecoration(30)
        grid_region.addItemDecoration(spaceDecoration)

    }

    private fun getRegionItems(): ArrayList<RegionItem> {
        val regions = resources.getStringArray(R.array.regions)
        val regions_full = resources.getStringArray(R.array.regions_full)
        val regionItems: ArrayList<RegionItem> = ArrayList<RegionItem>()
        for (i: Int in regions.indices) {
            val item = RegionItem()
            item.shortName = regions[i]
            item.longName = regions_full[i]
            regionItems.add(item)
        }

        return regionItems
    }
}