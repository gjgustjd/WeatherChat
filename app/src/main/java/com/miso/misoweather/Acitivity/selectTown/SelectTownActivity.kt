package com.miso.misoweather.Acitivity.selectTown

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
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.Acitivity.getnickname.SelectNickNameActivity
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListData
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.Acitivity.selectArea.RecyclerTownsAdapter
import com.miso.misoweather.Acitivity.selectArea.SelectAreaActivity
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class SelectTownActivity : MisoActivity() {
    lateinit var binding: ActivitySelectRegionBinding
    lateinit var grid_region: RecyclerView
    lateinit var list_towns: RecyclerView
    lateinit var btn_back: ImageButton
    lateinit var btn_next: Button
    lateinit var selectedRegion: String
    lateinit var townRequestResult: RegionListResponseDto
    lateinit var recyclerAdapter: RecyclerTownsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectedRegion = intent.getStringExtra("region") ?: getShortRegionName()
        initializeViews()
        getTownList()

    }

    fun getShortRegionName(): String {
        var regionsList = resources.getStringArray(R.array.regions).toList()
        var regionsFullList = resources.getStringArray(R.array.regions_full).toList()
        var regionShortName =
            regionsList.get(regionsFullList.indexOf(getPreference("BigScaleRegion")!!))
        return regionShortName
    }

    fun initializeViews() {
        grid_region = binding.gridRegions
        list_towns = binding.recyclerTowns

        grid_region.visibility = INVISIBLE
        list_towns.visibility = VISIBLE
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener() {
            startActivity(Intent(this, SelectRegionActivity::class.java))
            transferToBack()
            finish()
        }
        btn_next.setOnClickListener()
        {
            try {
                var selectedRegion = recyclerAdapter.getSelectedItem()
                var midScaleRegion = selectedRegion.midScale
                var bigScaleRegion = selectedRegion.bigScale
                var defaultRegionId = selectedRegion.id.toString()
                addPreferencePair("BigScaleRegion", bigScaleRegion)
                addPreferencePair("MidScaleRegion", midScaleRegion)
                addPreferencePair("defaultRegionId", defaultRegionId)

                lateinit var intent: Intent
                if (selectedRegion.midScale.contains("선택 안 함"))
                    intent = Intent(this, SelectNickNameActivity::class.java)
                else
                    intent = Intent(this, SelectAreaActivity::class.java)
                intent.putExtra("region", recyclerAdapter.getSelectedItem().bigScale)
                intent.putExtra("town", midScaleRegion)
                startActivity(intent)
                transferToNext()
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                savePreferences()
            }
        }
    }

    fun getTownList() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callGetTownList = api.getCity(selectedRegion)

        callGetTownList.enqueue(object : Callback<RegionListResponseDto> {
            override fun onResponse(
                call: Call<RegionListResponseDto>,
                response: Response<RegionListResponseDto>
            ) {
                Log.i("getTownList", "2단계 지역 받아오기 성공")
                townRequestResult = response.body()!!
                setRecyclerTowns()
            }

            override fun onFailure(call: Call<RegionListResponseDto>, t: Throwable) {
                Log.i("getTownList", "실패 : $t")
            }
        })
    }

    fun setRecyclerTowns() {
        try {

            var regionListData = townRequestResult.data as RegionListData
            var townList: List<Region> = regionListData.regionList
            recyclerAdapter = RecyclerTownsAdapter(this@SelectTownActivity, townList)
            list_towns.adapter = recyclerAdapter
            list_towns.layoutManager = LinearLayoutManager(this)
            val spaceDecoration = DividerItemDecoration(applicationContext, VERTICAL)
            list_towns.addItemDecoration(spaceDecoration)
            var currentTown = getPreference("MidScaleRegion")
            if (!currentTown.equals(""))
                recyclerAdapter.selectItem(townList.indexOf(townList.first() {
                    it.midScale.equals(
                        currentTown
                    )
                }))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}