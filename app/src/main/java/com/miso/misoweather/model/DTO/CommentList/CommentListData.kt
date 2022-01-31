package com.miso.misoweather.model.DTO.CommentList

data class CommentListData(
    val commentList: List<Comment>,
    val hasNext: Boolean
)