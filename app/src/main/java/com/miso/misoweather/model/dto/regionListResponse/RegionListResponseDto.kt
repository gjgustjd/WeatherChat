package com.miso.misoweather.model.dto.regionListResponse

data class RegionListResponseDto(
    val data: RegionListData,
    val message: String,
    val status: String
)