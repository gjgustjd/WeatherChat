package com.miso.misoweather.activity.updateRegion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.activity.selectRegion.RecyclerRegionsAdapter
import com.miso.misoweather.databinding.ActivityUpdateRegionBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class UpdateRegionActivity : MisoActivity() {
    private val viewModel: UpdateRegionViewModel by viewModels()
    private lateinit var binding: ActivityUpdateRegionBinding
    private lateinit var grid_region: RecyclerView
    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button
    private lateinit var currentRegion: String

    @Inject
    lateinit var gridAdapter: RecyclerRegionsAdapter

    @Inject
    lateinit var gridLayoutManager: GridLayoutManager

    @Inject
    lateinit var decoration: VerticalSpaceItemDecoration

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
                viewModel.updateSurveyRegion(gridAdapter.getSelectedItemShortName())
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
        val regionList = resources.getStringArray(R.array.regions)
        gridAdapter.selectedIndex = regionList.indexOf(currentRegion)
        grid_region.apply {
            adapter = gridAdapter
            layoutManager = gridLayoutManager
            addItemDecoration(decoration)
        }
    }
}