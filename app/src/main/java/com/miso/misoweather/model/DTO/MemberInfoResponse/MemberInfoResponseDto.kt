package com.miso.misoweather.model.DTO.MemberInfoResponse

data class MemberInfoResponseDto(
    val data: MemberInfoDto,
    val message: String,
    val status: String
)