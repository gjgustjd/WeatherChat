package com.miso.misoweather.activity.selectRegion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.miso.misoweather.R
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.activity.login.LoginActivity
import com.miso.misoweather.activity.selectTown.SelectTownActivity
import com.miso.misoweather.common.RegionActivity
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SelectRegionActivity : RegionActivity() {
    private lateinit var binding: ActivitySelectRegionBinding
    private val aPurpose by lazy{ intent.getStringExtra("for") ?: "" }
    @Inject
    lateinit var repository: MisoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_region)
        binding.activity = this
        binding.lifecycleOwner = this
        setContentView(binding.root)
    }


    override fun goToNextActivity() {
        super.goToNextActivity()
        try {
            val gridAdapter = binding.gridRegions.adapter as RecyclerRegionsAdapter
            if (gridAdapter.selectedIndex.value == -1) Toast.makeText(
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
}