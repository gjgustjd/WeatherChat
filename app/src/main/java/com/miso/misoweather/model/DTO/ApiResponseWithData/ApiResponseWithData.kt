package com.miso.misoweather.model.DTO.ApiResponseWithData

data class ApiResponseWithData <T>(
    val data: Class<T>,
    val message: String,
    val status: String
)