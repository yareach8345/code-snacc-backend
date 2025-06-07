package com.yareach.codesnaccbackend.dto.comment

import com.yareach.codesnaccbackend.dto.user.WriterInfoDto
import java.time.LocalDateTime

data class CommentDto(
    val commentId: Int,
    val content: String,
    val writer: WriterInfoDto,
    val writtenAt: LocalDateTime,
)
