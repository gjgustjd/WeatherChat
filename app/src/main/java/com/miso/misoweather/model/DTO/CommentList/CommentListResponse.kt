package com.miso.misoweather.model.DTO.CommentList

data class CommentListResponse(
    val data: CommentListData,
    val message: String,
    val status: String
)