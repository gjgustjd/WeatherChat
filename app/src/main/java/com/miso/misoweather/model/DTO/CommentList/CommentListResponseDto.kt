package com.miso.misoweather.model.DTO.CommentList

data class CommentListResponseDto(
    val data: CommentListData,
    val message: String,
    val status: String
)