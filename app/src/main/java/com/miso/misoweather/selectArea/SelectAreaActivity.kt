package com.miso.misoweather.selectArea

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
import com.miso.misoweather.getnickname.SelectNickNameActivity
import com.miso.misoweather.model.DTO.ApiResponseWithData.ApiResponseWithData
import com.miso.misoweather.model.DTO.ApiResponseWithData.Region
import com.miso.misoweather.model.DTO.NicknameResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.selectRegion.SelectRegionActivity
import com.miso.misoweather.selectTown.RecyclerAreaAdapter
import com.miso.misoweather.selectTown.SelectTownActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectAreaActivity :MisoActivity(){
    lateinit var binding:ActivitySelectRegionBinding
    lateinit var grid_region:RecyclerView
    lateinit var list_towns:RecyclerView
    lateinit var btn_back:ImageButton
    lateinit var btn_next: Button
    lateinit var selectedRegion:String
    lateinit var selectedTown:String
    lateinit var townRequestResult:ApiResponseWithData
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectedRegion = intent.getStringExtra("region")?:""
        selectedTown = intent.getStringExtra("town")?:""
        initializeViews()
        getAreaList()

    }
    fun initializeViews()
    {
        grid_region = binding.gridRegions
        list_towns = binding.recyclerTowns

        grid_region.visibility= INVISIBLE
        list_towns.visibility= VISIBLE
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        btn_back.setOnClickListener(){
            startActivity(Intent(this, SelectTownActivity::class.java))
            transferToBack()
            finish()
        }
       btn_next.setOnClickListener()
       {
           startActivity(Intent(this,SelectNickNameActivity::class.java))
           transferToNext()
           finish()
       }
    }

    fun getAreaList()
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callGetTownList = api.getArea(selectedRegion,selectedTown)

        callGetTownList.enqueue(object : Callback<ApiResponseWithData> {
            override fun onResponse(
                call: Call<ApiResponseWithData>,
                response: Response<ApiResponseWithData>) {
                Log.i("getAreaList","3단계 지역 받아오기 성공")
                townRequestResult = response.body()!!
                setRecyclerTowns()
            }

            override fun onFailure(call: Call<ApiResponseWithData>, t: Throwable) {
                Log.i("getTownList","실패 : $t")
            }
        })
    }
    fun setRecyclerTowns()
    {
        var townList:List<Region> = townRequestResult.data.regionList
        var adapter = RecyclerAreaAdapter(this@SelectAreaActivity,townList)
        list_towns.adapter=adapter
        list_towns.layoutManager = LinearLayoutManager(this)
        val spaceDecoration = DividerItemDecoration(applicationContext,VERTICAL)
        list_towns.addItemDecoration(spaceDecoration)
    }
}