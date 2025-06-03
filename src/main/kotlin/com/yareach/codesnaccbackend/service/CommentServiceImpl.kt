package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.comment.CommentDto
import com.yareach.codesnaccbackend.dto.comment.PostCommentDto
import com.yareach.codesnaccbackend.entity.CommentEntity
import com.yareach.codesnaccbackend.exception.CommentNotFoundException
import com.yareach.codesnaccbackend.exception.PostNotFoundException
import com.yareach.codesnaccbackend.exception.ResourceOwnershipException
import com.yareach.codesnaccbackend.exception.UserNotFoundException
import com.yareach.codesnaccbackend.extensions.findOrThrow
import com.yareach.codesnaccbackend.extensions.toDto
import com.yareach.codesnaccbackend.repository.CommentRepository
import com.yareach.codesnaccbackend.repository.PostRepository
import com.yareach.codesnaccbackend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl(
    val commentRepository: CommentRepository,
    val userRepository: UserRepository,
    private val postRepository: PostRepository,
): CommentService {
    override fun getCommentsByPostId(postId: Int): List<CommentDto> {
        val isThePostExists = postRepository.existsById(postId)

        if(!isThePostExists)
            throw PostNotFoundException(postId)

        return commentRepository
            .findByPostIdOrderByWrittenAtDesc(postId)
            .map { it.toDto() }
    }

    @Transactional
    override fun postCommentByPostId(
        postId: Int,
        userId: String,
        newCommentDto: PostCommentDto
    ): Int {
        val userEntity = userRepository.findOrThrow(userId) { throw UserNotFoundException(userId) }
        val postEntity = postRepository.findOrThrow(postId) { throw PostNotFoundException(postId) }
        val commentEntity = CommentEntity(
            content = newCommentDto.content,
            post = postEntity,
            writer = userEntity
        )
        val comment = commentRepository.save(commentEntity)
        return comment.id!!
    }

    @Transactional
    override fun deleteComment(commentId: Int, userId: String) {
        val comment = commentRepository.findOrThrow(commentId) { throw CommentNotFoundException(commentId) }

        if(comment.writer.id != userId) {
            throw ResourceOwnershipException("해당 댓글에 접근할 수 없습니다.")
        }

        commentRepository.deleteById(commentId)
    }

    override fun getComment(commentId: Int): CommentDto {
        return commentRepository.findOrThrow(commentId) { throw CommentNotFoundException(commentId) }.toDto()
    }
}