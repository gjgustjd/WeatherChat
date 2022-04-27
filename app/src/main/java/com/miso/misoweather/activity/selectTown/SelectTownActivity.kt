package com.miso.misoweather.activity.selectTown

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
import com.miso.misoweather.activity.selectArea.RecyclerAreaAdapter
import com.miso.misoweather.model.dto.regionListResponse.RegionListResponseDto
import com.miso.misoweather.model.dto.Region
import com.miso.misoweather.activity.selectArea.SelectAreaActivity
import com.miso.misoweather.activity.selectRegion.SelectRegionActivity
import com.miso.misoweather.common.RegionActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class SelectTownActivity : RegionActivity() {
    private val viewModel: SelectTownViewModel by viewModels()
    private lateinit var binding: ActivitySelectRegionBinding
    private val aPurpose by lazy { intent.getStringExtra("for") ?: "" }
    private val selectedRegion by lazy { intent.getStringExtra("region") ?: getShortRegionName() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_region)
        binding.activity = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initializeScale()
        getTownList()
    }

    private fun getShortRegionName(): String {
        val regionsList = resources.getStringArray(R.array.regions).toList()
        val regionsFullList = resources.getStringArray(R.array.regions_full).toList()
        return regionsList[regionsFullList.indexOf(viewModel.bigScaleRegion)]
    }

    fun initializeScale(){
        bigScale = viewModel.bigScaleRegion
        midScale = viewModel.midScaleRegion
    }

    override fun goToNextActivity() {
        super.goToNextActivity()
        try {
            val adapter = binding.recyclerTowns.adapter as RecyclerAreaAdapter
            val selectedRegion = adapter.getSelectedItem()
            val midScaleRegion = selectedRegion.midScale
            viewModel.addRegionPreferences(selectedRegion)

            lateinit var intent: Intent
            if (selectedRegion.midScale.contains("선택 안 함")) {
                if (aPurpose.equals("change")) {
                    changeRegion(adapter)
                } else {
                    viewModel.addRegionPreferences(selectedRegion)
                    viewModel.updateSmallScaleRegion("")
                    intent = Intent(this, SelectNickNameActivity::class.java)
                    intent.putExtra("RegionId", selectedRegion.id.toString())
                }
            } else
                intent = Intent(this, SelectAreaActivity::class.java)

            intent.apply {
                putExtra("for", aPurpose)
                putExtra("region", adapter.getSelectedItem().bigScale)
                putExtra("town", midScaleRegion)
            }
            startActivity(intent)
            transferToNext()
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                    startActivity(Intent(this@SelectTownActivity, HomeActivity::class.java))
                    transferToNext()
                    finish()
                }
            }
        }
    }

    override fun doBack() {
        val intent = Intent(this, SelectRegionActivity::class.java)
        intent.putExtra("for", aPurpose)
        startActivity(intent)
        transferToBack()
        finish()
    }

    private fun getTownList() {
        lifecycleScope.launch {
            viewModel.getTownList(selectedRegion)
            {
                if (it.isSuccessful) {
                    Log.i("getTownList", "2단계 지역 받아오기 성공")
                    recyclerData.value = it.body()!!.data.regionList
                }
            }
        }
    }
}