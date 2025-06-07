package com.yareach.codesnaccbackend.dto.post

import java.time.LocalDateTime

data class PostInfoResponseDto(
    val id: Int,
    val writerId: String,
    val writerNickname: String?,
    val writerIcon: String?,
    val title: String,
    val code: String,
    val language: String,
    val content: String,
    val writtenAt: LocalDateTime,
    val tags: List<String>,
    val commentCnt: Int,
    val recommendCnt: Int,
    val didIRecommend: Boolean
)