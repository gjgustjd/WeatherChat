package com.miso.misoweather.Acitivity.selectTown

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout.VERTICAL
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
import com.miso.misoweather.Acitivity.selectArea.RecyclerTownsAdapter
import com.miso.misoweather.Acitivity.selectArea.SelectAreaActivity
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import com.miso.misoweather.model.MisoRepository
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
    lateinit var aPurpose: String
    lateinit var repository: MisoRepository
    lateinit var viewModel: SelectTownViewModel

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
        repository = MisoRepository.getInstance(applicationContext)
        viewModel = SelectTownViewModel(repository)
        aPurpose = intent.getStringExtra("for") ?: ""
        grid_region = binding.gridRegions
        list_towns = binding.recyclerTowns

        grid_region.visibility = INVISIBLE
        list_towns.visibility = VISIBLE
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener() {
            var intent = Intent(this, SelectRegionActivity::class.java)
            intent.putExtra("for", aPurpose)
            startActivity(intent)
            transferToBack()
            finish()
        }
        btn_next.setOnClickListener()
        {
            doBack()
        }
    }

    fun changeRegion() {
        repository.updateRegion(
            getPreference("misoToken")!!,
            recyclerAdapter.getSelectedItem().id,
            { call, response ->
                try {
                    Log.i("changeRegion", "성공")
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
            },
            { call, response -> },
            { call, t ->
//                Log.i("changeRegion", "실패")
            }
        )
    }

    override fun doBack() {
        try {
            var selectedRegion = recyclerAdapter.getSelectedItem()
            var midScaleRegion = selectedRegion.midScale
            var bigScaleRegion = selectedRegion.bigScale
            addPreferencePair("BigScaleRegion", bigScaleRegion)
            addPreferencePair("MidScaleRegion", midScaleRegion)

            lateinit var intent: Intent
            if (selectedRegion.midScale.contains("선택 안 함")) {
                if (aPurpose.equals("change")) {
                    changeRegion()
                } else {
                    removePreference("SmallScaleRegion")
                    intent = Intent(this, SelectNickNameActivity::class.java)
                    intent.putExtra("RegionId", selectedRegion.id.toString())
                }
            } else
                intent = Intent(this, SelectAreaActivity::class.java)

            intent.putExtra("for", aPurpose)
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

    fun getTownList() {
        viewModel.getTownList(selectedRegion)
        viewModel.townRequestResult.observe(this, Observer {
            if(it==null)
            {

            }
            else {
                if(it.isSuccessful) {
                    try {
                        Log.i("getTownList", "2단계 지역 받아오기 성공")
                        townRequestResult = it.body()!!
                        setRecyclerTowns()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else
                {
                }
            }
        })
    }

    fun setRecyclerTowns() {
        try {
            var regionListData = townRequestResult.data
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