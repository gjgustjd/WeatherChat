package com.miso.misoweather.model.interfaces

import com.miso.misoweather.model.DTO.NicknameResponseDto
import com.miso.misoweather.model.DTO.SignUpRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MisoWeatherAPI {
 @GET("api/member/nickname")
 fun getNickname(): Call<NicknameResponseDto>

 @POST("api/member")
 fun registerMember(@Body body:SignUpRequestDto,@Query("socialToken") socialToken:String):Call<NicknameResponseDto>
}