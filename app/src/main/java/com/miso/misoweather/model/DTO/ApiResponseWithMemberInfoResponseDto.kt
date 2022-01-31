package com.miso.misoweather.model.DTO

data class ApiResponseWithMemberInfoResponseDto(
    val data: MemberInfoResponseDto,
    val message: String,
    val status: String
)