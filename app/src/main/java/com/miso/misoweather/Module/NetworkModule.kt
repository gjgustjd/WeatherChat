package com.miso.misoweather.Module

import com.google.gson.GsonBuilder
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun getRetrofitApi(): MisoWeatherAPI {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(MisoActivity.MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(MisoWeatherAPI::class.java)
    }
}