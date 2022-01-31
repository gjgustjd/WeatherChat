package com.miso.misoweather.model.interfaces

import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.model.DTO.ApiResponseWithData.ApiResponseWithData
import com.miso.misoweather.model.DTO.ApiResponseWithData.Region
import com.miso.misoweather.model.DTO.ApiResponseWithData.RegionListData
import retrofit2.Call
import retrofit2.http.*

interface MisoWeatherAPI {
 @GET("api/member/nickname")
 fun getNickname(): Call<NicknameResponseDto>

 @POST("api/member")
 fun registerMember(@Body body:SignUpRequestDto,@Query("socialToken") socialToken:String):Call<GeneralResponseDto>

 @GET("api/region/{bigScaleRegion}")
 fun getCity(@Path("bigScaleRegion") bigScaleRegion:String):Call<ApiResponseWithData<RegionListData>>

 @GET("api/region/{bigScaleRegion}/{midScaleRegion}")
 fun getArea(@Path("bigScaleRegion") bigScaleRegion:String,
 @Path("midScaleRegion")midScaleRegion:String):Call<ApiResponseWithData<RegionListData>>

 @POST("api/member/token")
 fun reIssueMisoToken(@Body body: LoginRequestDto, @Query("socialToken")socialToken: String):Call<GeneralResponseDto>

 @GET("api/member")
 fun getUserInfo(@Header("serverToken") serverToken:String):Call<ApiResponseWithMemberInfoResponseDto>
}