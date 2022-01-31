package com.miso.misoweather.model.DTO.RegionListResponse

data class RegionListResponseDto(
    val data: RegionListData,
    val message: String,
    val status: String
)