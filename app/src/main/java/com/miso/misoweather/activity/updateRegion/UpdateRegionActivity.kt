package com.miso.misoweather.activity.updateRegion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.activity.selectRegion.RecyclerRegionsAdapter
import com.miso.misoweather.databinding.ActivityUpdateRegionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class UpdateRegionActivity : MisoActivity() {
    private val viewModel: UpdateRegionViewModel by viewModels()
    private lateinit var binding: ActivityUpdateRegionBinding
    val currentRegion by lazy { intent.getStringExtra("region") ?: viewModel.bigScaleRegion }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_region)
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
    }

    fun updateAndgoBack() {
        val recyclerAdapter = (binding.gridRegions.adapter as RecyclerRegionsAdapter)
        viewModel.updateSurveyRegion(recyclerAdapter.getSelectedItemShortName())
        doBack()
    }

    override fun doBack() {
        startActivity(Intent(this, ChatMainActivity::class.java))
        transferToBack()
        finish()
    }
}