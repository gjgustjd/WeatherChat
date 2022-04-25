package com.miso.misoweather.model.dto.commentList

data class CommentListResponseDto(
    val data: CommentListData,
    val message: String,
    val status: String
)