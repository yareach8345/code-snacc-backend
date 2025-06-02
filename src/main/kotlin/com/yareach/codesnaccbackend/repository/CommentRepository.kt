package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository: JpaRepository<CommentEntity, Int> {
    fun findByPostIdOrderByWrittenAtDesc(postId: Int): List<CommentEntity>
}