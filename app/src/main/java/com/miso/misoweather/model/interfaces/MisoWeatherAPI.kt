package com.miso.misoweather.model.interfaces

import com.miso.misoweather.model.DTO.NicknameResponseDto
import retrofit2.Call
import retrofit2.http.GET

interface MisoWeatherAPI {
 @GET("api/member/nickname")
 fun getNickname(): Call<NicknameResponseDto>
}