package com.miso.misoweather.model.dto.commentList

data class Comment(
    val bigScale: String,
    val content: String,
    val createdAt: String,
    val deleted: Boolean,
    val id: Int,
    val emoji:String,
    val nickname: String
)