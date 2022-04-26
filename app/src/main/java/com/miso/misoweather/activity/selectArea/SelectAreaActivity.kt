package com.miso.misoweather.activity.selectArea

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout.VERTICAL
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.activity.getnickname.SelectNickNameActivity
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.model.dto.Region
import com.miso.misoweather.activity.selectTown.SelectTownActivity
import com.miso.misoweather.common.RegionActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class SelectAreaActivity : RegionActivity() {
    private val viewModel: SelectAreaViewModel by viewModels()
    private lateinit var binding: ActivitySelectRegionBinding
    private val selectedRegion by lazy {
        intent.getStringExtra("region") ?: viewModel.bigScaleRegion
    }
    private val selectedTown by lazy { intent.getStringExtra("town") ?: viewModel.midScaleRegion }
    private val aPurpose by lazy { intent.getStringExtra("for") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        initializeScale()
        getAreaList()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_region)
        binding.activity = this
        binding.lifecycleOwner = this
    }

    fun initializeScale() {
        bigScale = viewModel.bigScaleRegion
        midScale = viewModel.midScaleRegion
        smallScale = viewModel.smallScaleRegion
    }

    override fun goToNextActivity() {
        super.goToNextActivity()
        try {
            val recyclerAdapter = binding.recyclerTowns.adapter as RecyclerAreaAdapter

            if (aPurpose.equals("change")) {
                changeRegion(recyclerAdapter)
            } else {
                viewModel.addRegionPreferences(recyclerAdapter.getSelectedItem())
                val intent = Intent(this, SelectNickNameActivity::class.java)
                intent.putExtra("RegionId", recyclerAdapter.getSelectedItem().id.toString())
                startActivity(intent)
                transferToNext()
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun doBack() {
        val intent = Intent(this, SelectTownActivity::class.java)
        intent.putExtra("for", aPurpose)
        startActivity(intent)
        transferToBack()
        finish()
    }


    private fun changeRegion(adapter: RecyclerAreaAdapter) {
        val currentItem = adapter.getSelectedItem()
        lifecycleScope.launch {
            viewModel.updateRegion(
                currentItem,
                currentItem.id
            )
            {
                if (it.isSuccessful) {
                    startActivity(Intent(this@SelectAreaActivity, HomeActivity::class.java))
                    transferToNext()
                    finish()
                }
            }
        }
    }

    private fun getAreaList() {
        lifecycleScope.launch {
            viewModel.getAreaList(
                selectedRegion,
                selectedTown
            )
            {
                if (it.isSuccessful) {
                    Log.i("getAreaList", "3단계 지역 받아오기 성공")
                    recyclerData.value = it.body()!!.data.regionList
                }
            }
        }
    }

}