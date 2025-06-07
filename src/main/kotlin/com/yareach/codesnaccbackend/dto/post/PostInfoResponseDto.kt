package com.yareach.codesnaccbackend.dto.post

import com.yareach.codesnaccbackend.dto.user.WriterInfoDto
import java.time.LocalDateTime

data class PostInfoResponseDto(
    val id: Int,
    val writer: WriterInfoDto,
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