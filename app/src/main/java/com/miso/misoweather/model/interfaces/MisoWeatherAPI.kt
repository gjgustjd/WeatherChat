package com.miso.misoweather.model.interfaces

import com.miso.misoweather.model.DTO.ApiResponseWithData.ApiResponseWithData
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DTO.NicknameResponseDto
import com.miso.misoweather.model.DTO.SignUpRequestDto
import retrofit2.Call
import retrofit2.http.*

interface MisoWeatherAPI {
 @GET("api/member/nickname")
 fun getNickname(): Call<NicknameResponseDto>

 @POST("api/member")
 fun registerMember(@Body body:SignUpRequestDto,@Query("socialToken") socialToken:String):Call<GeneralResponseDto>

 @GET("api/region/{bigScaleRegion}")
 fun getCity(@Path("bigScaleRegion") bigScaleRegion:String):Call<ApiResponseWithData>

 @GET("api/region/{bigScaleRegion}/{midScaleRegion}")
 fun getArea(@Path("bigScaleRegion") bigScaleRegion:String,
 @Path("midScaleRegion")midScaleRegion:String):Call<ApiResponseWithData>

 @POST("api/member/token")
 fun reIssueMisoToken(@Body body: LoginRequestDto, @Query("socialToken")socialToken: String):Call<GeneralResponseDto>
}