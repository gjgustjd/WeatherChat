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
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.Acitivity.selectTown.RecyclerAreaAdapter
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.TransportManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    lateinit var townRequestResult: RegionListResponseDto
    lateinit var recyclerAdapter: RecyclerAreaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectedRegion = intent.getStringExtra("region") ?: getPreference("BigScaleRegion")!!
        selectedTown = intent.getStringExtra("town") ?: getPreference("MidScaleRegion")!!
        initializeViews()
        getAreaList()

    }

    fun initializeViews() {
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

    fun addRegionPreferences() {
        var selectedRegion = recyclerAdapter.getSelectedItem()
        var midScaleRegion = selectedRegion.midScale
        var bigScaleRegion = selectedRegion.bigScale
        var smallScaleRegion = selectedRegion.smallScale
        addPreferencePair("BigScaleRegion", bigScaleRegion)
        addPreferencePair("MidScaleRegion", midScaleRegion)
        addPreferencePair("SmallScaleRegion", smallScaleRegion)
        savePreferences()
    }

    fun changeRegion() {
        val callChangeRegion = TransportManager.getRetrofitApiObject<GeneralResponseDto>()
            .updateRegion(getPreference("misoToken")!!, recyclerAdapter.getSelectedItem().id)

        TransportManager.requestApi(callChangeRegion, { call, response ->
            try {
                Log.i("changeRegion", "성공")
                addRegionPreferences()
                addPreferencePair(
                    "defaultRegionId",
                    recyclerAdapter.getSelectedItem().id.toString()
                )
                savePreferences()
                startActivity(Intent(this, HomeActivity::class.java))
                transferToNext()
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { call, t ->
            Log.i("changeRegion", "실패")
        })
    }

    fun getAreaList() {
        val callGetTownList = TransportManager.getRetrofitApiObject<RegionListResponseDto>()
            .getArea(selectedRegion, selectedTown)

        TransportManager.requestApi(callGetTownList, { call, response ->
            try {
                Log.i("getAreaList", "3단계 지역 받아오기 성공")
                townRequestResult = response.body()!!
                setRecyclerTowns()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { call, t ->
            Log.i("결과", "실패 : $t")
        })
    }

    fun setRecyclerTowns() {
        try {
            var regionListData = townRequestResult.data
            var townList: List<Region> = regionListData.regionList
            recyclerAdapter = RecyclerAreaAdapter(this@SelectAreaActivity, townList)
            list_towns.adapter = recyclerAdapter
            list_towns.layoutManager = LinearLayoutManager(this)
            val spaceDecoration = DividerItemDecoration(applicationContext, VERTICAL)
            list_towns.addItemDecoration(spaceDecoration)
            var currentArea = getPreference("SmallScaleRegion")
            if (!currentArea.equals(""))
                recyclerAdapter.selectItem(townList.indexOf(townList.first() {
                    it.smallScale.equals(currentArea)
                }))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}