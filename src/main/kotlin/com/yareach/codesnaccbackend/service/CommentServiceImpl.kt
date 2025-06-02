package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.comment.CommentDto
import com.yareach.codesnaccbackend.entity.CommentEntity
import com.yareach.codesnaccbackend.extensions.toDto
import com.yareach.codesnaccbackend.repository.CommentRepository
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl(
    val commentRepository: CommentRepository
): CommentService {
    override fun getCommentsByPostId(postId: Int): List<CommentDto> {
        return commentRepository
            .findByPostIdOrderByWrittenAtDesc(postId)
            .map { it.toDto() }
    }
}