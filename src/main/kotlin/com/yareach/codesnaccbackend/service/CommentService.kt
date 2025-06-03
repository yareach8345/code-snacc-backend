package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.comment.CommentDto
import com.yareach.codesnaccbackend.dto.comment.PostCommentDto

interface CommentService {
    fun getCommentsByPostId(postId: Int): List<CommentDto>

    fun postCommentByPostId(postId: Int, userId: String, newCommentDto: PostCommentDto): Int

    fun deleteComment(commentId: Int, userId: String)
}