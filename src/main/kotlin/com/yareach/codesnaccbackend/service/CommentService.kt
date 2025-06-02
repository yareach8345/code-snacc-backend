package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.comment.CommentDto

interface CommentService {
    fun getCommentsByPostId(postId: Int): List<CommentDto>
}