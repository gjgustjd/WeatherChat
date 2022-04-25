package com.miso.misoweather.model.dto.memberInfoResponse

data class MemberInfoResponseDto(
    val data: MemberInfoDto,
    val message: String,
    val status: String
)