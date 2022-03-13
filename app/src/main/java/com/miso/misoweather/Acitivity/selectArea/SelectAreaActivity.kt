package com.miso.misoweather.Acitivity.selectArea

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout.VERTICAL
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.Acitivity.getnickname.SelectNickNameActivity
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import com.miso.misoweather.model.MisoRepository
import java.lang.Exception

class SelectAreaActivity : MisoActivity() {
    lateinit var binding: ActivitySelectRegionBinding
    lateinit var grid_region: RecyclerView
    lateinit var list_towns: RecyclerView
    lateinit var btn_back: ImageButton
    lateinit var btn_next: Button
    lateinit var selectedRegion: String
    lateinit var selectedTown: String
    lateinit var aPurpose: String
    lateinit var recyclerAdapter: RecyclerAreaAdapter
    lateinit var viewModel: SelectAreaViewModel

    lateinit var smallScaleRegion: String
    lateinit var midScaleRegion: String
    lateinit var bigScaleRegion: String
    var isAllInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProperties()

    }

    fun initializeProperties() {
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
        viewModel = SelectAreaViewModel(MisoRepository.getInstance(applicationContext))
        viewModel.updateProperties()
        viewModel.smallScaleRegion.observe(this, {
            smallScaleRegion = it!!
            checkinitializedAll()
        })
        viewModel.midScaleRegion.observe(this, {
            midScaleRegion = it!!
            checkinitializedAll()
        })
        viewModel.bigScaleRegion.observe(this, {
            bigScaleRegion = it!!
            checkinitializedAll()
        })
    }

    fun initializeViews() {
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
                    var intent: Intent?
                    intent = Intent(this, SelectNickNameActivity::class.java)
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
        var intent = Intent(this, SelectTownActivity::class.java)
        intent.putExtra("for", aPurpose)
        startActivity(intent)
        transferToBack()
        finish()
    }


    fun changeRegion() {
        viewModel.updateRegion(
            recyclerAdapter.getSelectedItem(),
            recyclerAdapter.getSelectedItem().id
        )
        viewModel.updateRegionResponse.observe(this, {
            if (it == null) {
            } else {
                if (it.isSuccessful) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    transferToNext()
                    finish()
                } else {
                }
            }
        })
    }

    fun getAreaList() {
        viewModel.getAreaList(
            selectedRegion,
            selectedTown
        )
        viewModel.areaRequestResult.observe(this, {
            if (it == null) {
            } else {
                if (it.isSuccessful) {
                    try {
                        Log.i("getAreaList", "3단계 지역 받아오기 성공")
                        setRecyclerTowns(it!!.body()!!.data.regionList)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                }
            }
        })
    }

    fun setRecyclerTowns(townList: List<Region>) {
        try {
            recyclerAdapter = RecyclerAreaAdapter(this@SelectAreaActivity, townList)
            list_towns.adapter = recyclerAdapter
            list_towns.layoutManager = LinearLayoutManager(this)
            val spaceDecoration = DividerItemDecoration(applicationContext, VERTICAL)
            list_towns.addItemDecoration(spaceDecoration)
            if (!smallScaleRegion.equals(""))
                recyclerAdapter.selectItem(townList.indexOf(townList.first {
                    it.smallScale.equals(smallScaleRegion)
                }))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}