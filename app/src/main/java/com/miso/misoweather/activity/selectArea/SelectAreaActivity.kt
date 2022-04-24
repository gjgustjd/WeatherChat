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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.activity.getnickname.SelectNickNameActivity
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.activity.selectTown.SelectTownActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        getAreaList()
    }

    private fun initializeViews() {
        selectedRegion = intent.getStringExtra("region") ?: viewModel.bigScaleRegion
        selectedTown = intent.getStringExtra("town") ?: viewModel.midScaleRegion
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
        val currentItem = recyclerAdapter.getSelectedItem()
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
                    try {
                        Log.i("getAreaList", "3단계 지역 받아오기 성공")
                        setRecyclerTowns(it.body()!!.data.regionList)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
            if (!viewModel.smallScaleRegion.equals(""))
                recyclerAdapter.selectItem(townList.indexOf(townList.first {
                    it.smallScale.equals(viewModel.smallScaleRegion)
                }))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}