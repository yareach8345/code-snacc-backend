package com.yareach.codesnaccbackend.dto.comment

import java.time.LocalDateTime

data class CommentDto(
    val commentId: Int,
    val content: String,
    val writerId: String,
    val writerNickname: String?,
    val writerIcon: String?,
    val writtenAt: LocalDateTime,
)
