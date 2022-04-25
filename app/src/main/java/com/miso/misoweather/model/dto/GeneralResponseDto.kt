package com.miso.misoweather.model.dto

data class GeneralResponseDto(
    val message: String,
    val status: String,
    val data:Any?
)