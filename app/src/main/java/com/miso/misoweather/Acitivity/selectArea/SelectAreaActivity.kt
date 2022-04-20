package com.miso.misoweather.Acitivity.selectArea

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.Acitivity.getnickname.SelectNickNameActivity
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class SelectAreaActivity : MisoActivity() {
    private val viewModel: SelectAreaViewModel by viewModels()
    private lateinit var binding: ActivitySelectRegionBinding
    private lateinit var grid_region: RecyclerView
    private lateinit var list_towns: RecyclerView
    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button
    private lateinit var selectedRegion: String
    private lateinit var selectedTown: String
    private lateinit var aPurpose: String
    private lateinit var recyclerAdapter: RecyclerAreaAdapter
    private lateinit var smallScaleRegion: String
    private lateinit var midScaleRegion: String
    private lateinit var bigScaleRegion: String
    private var isAllInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProperties()

    }

    private fun initializeProperties() {
        fun checkinitializedAll() {
            if (!isAllInitialized) {
                if (
                    this::smallScaleRegion.isInitialized &&
                    this::midScaleRegion.isInitialized &&
                    this::bigScaleRegion.isInitialized
                ) {
                    initializeViews()
                    getAreaList()
                    isAllInitialized = true
                }
            }
        }
        viewModel.updateProperties()
        viewModel.smallScaleRegion.observe(this) {
            smallScaleRegion = it!!
            checkinitializedAll()
        }
        viewModel.midScaleRegion.observe(this) {
            midScaleRegion = it!!
            checkinitializedAll()
        }
        viewModel.bigScaleRegion.observe(this) {
            bigScaleRegion = it!!
            checkinitializedAll()
        }
    }

    private fun initializeViews() {
        selectedRegion = intent.getStringExtra("region") ?: bigScaleRegion
        selectedTown = intent.getStringExtra("town") ?: midScaleRegion
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
                if (aPurpose.equals("change")) {
                    changeRegion()
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
    }

    override fun doBack() {
        val intent = Intent(this, SelectTownActivity::class.java)
        intent.putExtra("for", aPurpose)
        startActivity(intent)
        transferToBack()
        finish()
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

    private fun getAreaList() {
        viewModel.getAreaList(
            selectedRegion,
            selectedTown
        )
        viewModel.areaRequestResult.observe(this) {
            val responseDto = it as Response<RegionListResponseDto>
            if (it == null) {
            } else {
                if (it.isSuccessful) {
                    try {
                        Log.i("getAreaList", "3단계 지역 받아오기 성공")
                        setRecyclerTowns(responseDto.body()!!.data.regionList)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                }
            }
        }
    }

    private fun setRecyclerTowns(townList: List<Region>) {
        try {
            recyclerAdapter = RecyclerAreaAdapter(townList)
            list_towns.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(this@SelectAreaActivity)
                addItemDecoration(DividerItemDecoration(applicationContext, VERTICAL))
            }
            if (!smallScaleRegion.equals(""))
                recyclerAdapter.selectItem(townList.indexOf(townList.first {
                    it.smallScale == smallScaleRegion
                }))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}