package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.comment.CommentDto
import com.yareach.codesnaccbackend.dto.comment.CommentPostDto
import com.yareach.codesnaccbackend.dto.comment.CommentUpdateDto

interface CommentService {
    fun getCommentsByPostId(postId: Int): List<CommentDto>

    fun postCommentByPostId(postId: Int, userId: String, newCommentDto: CommentPostDto): Int

    fun deleteComment(commentId: Int, userId: String)

    fun getComment(commentId: Int): CommentDto

    fun updateComment(commentId: Int, userId: String, commentUpdateDto: CommentUpdateDto)
}