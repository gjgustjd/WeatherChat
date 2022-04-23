package com.miso.misoweather.Acitivity.selectTown

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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.Acitivity.getnickname.SelectNickNameActivity
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.Acitivity.selectArea.SelectAreaActivity
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class SelectTownActivity : MisoActivity() {
    private val viewModel: SelectTownViewModel by viewModels()
    private lateinit var binding: ActivitySelectRegionBinding
    private lateinit var grid_region: RecyclerView
    private lateinit var list_towns: RecyclerView
    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button
    private lateinit var selectedRegion: String
    private lateinit var townRequestResult: RegionListResponseDto
    private lateinit var recyclerAdapter: RecyclerTownsAdapter
    private lateinit var aPurpose: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        getTownList()
    }

    private fun getShortRegionName(): String {
        val regionsList = resources.getStringArray(R.array.regions).toList()
        val regionsFullList = resources.getStringArray(R.array.regions_full).toList()
        return regionsList[regionsFullList.indexOf(viewModel.bigScaleRegion)]
    }

    private fun initializeViews() {
        selectedRegion = intent.getStringExtra("region") ?: getShortRegionName()
        aPurpose = intent.getStringExtra("for") ?: ""
        grid_region = binding.gridRegions
        list_towns = binding.recyclerTowns

        grid_region.visibility = INVISIBLE
        list_towns.visibility = VISIBLE
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener() {
            doBack()
        }
        btn_next.setOnClickListener()
        {
            try {
                val selectedRegion = recyclerAdapter.getSelectedItem()
                val midScaleRegion = selectedRegion.midScale
                viewModel.addRegionPreferences(selectedRegion)

                lateinit var intent: Intent
                if (selectedRegion.midScale.contains("선택 안 함")) {
                    if (aPurpose.equals("change")) {
                        changeRegion()
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
                    putExtra("region", recyclerAdapter.getSelectedItem().bigScale)
                    putExtra("town", midScaleRegion)
                }
                startActivity(intent)
                transferToNext()
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun changeRegion() {
        var currentItem = recyclerAdapter.getSelectedItem()
        viewModel.updateRegion(
            currentItem,
            currentItem.id
        )
        viewModel.updateRegionResponse.observe(this) {
            if (it == null) {
            } else {
                if (it.isSuccessful) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    transferToNext()
                    finish()
                } else {
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
        viewModel.getTownList(selectedRegion)
        viewModel.townRequestResult.observe(this) {
            if (it == null) {
            } else {
                if (it.isSuccessful) {
                    try {
                        Log.i("getTownList", "2단계 지역 받아오기 성공")
                        townRequestResult = it.body()!! as RegionListResponseDto
                        setRecyclerTowns()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                }
            }
        }
    }

    private fun setRecyclerTowns() {
        try {
            val regionListData = townRequestResult.data
            val townList: List<Region> = regionListData.regionList
            recyclerAdapter = RecyclerTownsAdapter(this@SelectTownActivity, townList)
            list_towns.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(this@SelectTownActivity)
                addItemDecoration(DividerItemDecoration(applicationContext, VERTICAL))
                if (!viewModel.midScaleRegion.equals(""))
                    recyclerAdapter.selectItem(townList.indexOf(townList.first() {
                        it.midScale.equals(
                            viewModel.midScaleRegion
                        )
                    }))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}