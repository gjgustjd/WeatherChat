package com.miso.misoweather.model.DTO.CommentList

data class Comment(
    val bigScale: String,
    val content: String,
    val createdAt: String,
    val deleted: Boolean,
    val id: Int,
    val nickname: String
)