package com.miso.misoweather.model

import android.icu.text.UnicodeSetIterator
import android.util.Log
import com.google.gson.GsonBuilder
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class TransportManager {
    companion object {
        fun <T> getRetrofitApiObject(): MisoWeatherAPI {
            var gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl(MisoActivity.MISOWEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            val api = retrofit.create(MisoWeatherAPI::class.java)
            return api
        }

        fun <T> requestApi(
            callApi: Call<T>,
            onResponse: (Call<T>, Response<T>) -> Unit,
            onFailure: (Call<T>, Throwable) -> Unit
        ) {
            callApi.enqueue(object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>
                ) {
                    onResponse(call, response)
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    onFailure(call, t)
                }
            })
        }
    }
}