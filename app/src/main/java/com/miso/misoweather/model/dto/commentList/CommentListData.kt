package com.miso.misoweather.model.dto.commentList

data class CommentListData(
    val commentList: List<Comment>,
    val hasNext: Boolean
)