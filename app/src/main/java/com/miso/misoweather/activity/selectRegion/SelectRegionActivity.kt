package com.miso.misoweather.activity.selectRegion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.activity.login.LoginActivity
import com.miso.misoweather.activity.selectTown.SelectTownActivity
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SelectRegionActivity : MisoActivity() {
    private lateinit var binding: ActivitySelectRegionBinding
    private lateinit var grid_region: RecyclerView
    private lateinit var list_towns: RecyclerView
    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button
    private lateinit var aPurpose: String

    @Inject
    lateinit var repository: MisoRepository
    @Inject
    lateinit var gridAdapter: RecyclerRegionsAdapter
    @Inject
    lateinit var gridLayoutManager: GridLayoutManager
    @Inject
    lateinit var decoration: VerticalSpaceItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setRecyclerRegions()
    }

    private fun initializeViews() {
        aPurpose = intent.getStringExtra("for") ?: ""
        grid_region = binding.gridRegions
        list_towns = binding.recyclerTowns
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener {
            doBack()
        }
        btn_next.setOnClickListener {
            try {
                if (gridAdapter.selectedIndex == -1) Toast.makeText(
                    this,
                    "지역을 선택해 주세요",
                    Toast.LENGTH_SHORT
                ).show()
                else {
                    val bigScaleRegion = gridAdapter.getSelectedItemShortName()
                    repository.dataStoreManager.savePreference(
                        DataStoreManager.BIGSCALE_REGION, bigScaleRegion
                    )

                    val intent = Intent(this, SelectTownActivity::class.java)
                    intent.apply {
                        putExtra("for", aPurpose)
                        putExtra("region", bigScaleRegion)
                    }
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
        if (aPurpose.equals("change")) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            transferToBack()
            finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            transferToBack()
            finish()
        }
    }

    private fun setRecyclerRegions() {
        grid_region.apply {
            adapter = gridAdapter
            layoutManager = gridLayoutManager
            addItemDecoration(decoration)
        }
    }
}