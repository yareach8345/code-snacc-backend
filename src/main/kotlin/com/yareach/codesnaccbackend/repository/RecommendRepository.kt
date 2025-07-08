package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.RecommendEntity
import com.yareach.codesnaccbackend.entity.RecommendEntityId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RecommendRepository : JpaRepository<RecommendEntity, RecommendEntityId> {
    @Query("""
        select r 
        from RecommendEntity r
        where r.recommendId.user.id = :userId
    """)
    fun findByUserId(userId: String): List<RecommendEntity>

    @Query("""
        select count(r)
        from RecommendEntity r
        where r.recommendId.post.id = :postId
    """)
    fun countByPostId(postId: Int): Long

    @Query("""
        delete from RecommendEntity r where r.recommendId.post.id = :postId and r.recommendId.user.id = :userId
    """)
    fun deleteRecommendEntityBy(userId: String, postId: Int)
}