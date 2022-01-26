package com.miso.misoweather.model.DTO.ApiResponseWithData

data class ApiResponseWithData(
    val data: Data,
    val message: String,
    val status: String
)