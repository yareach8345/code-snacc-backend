package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.PostEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PostRepository: JpaRepository<PostEntity, Int> {
    fun findAllByDeletedIsFalseOrderByWrittenAtDesc(pageable: Pageable): List<PostEntity>

    fun findAllByDeletedIsFalseAndTitleContainingIgnoreCaseOrderByWrittenAtDesc(title: String, pageable: Pageable? = null): List<PostEntity>

    fun findAllByDeletedIsFalseAndWriterIdOrderByWrittenAtDesc(userId: String, pageable: Pageable? = null): List<PostEntity>

    fun findAllByDeletedIsFalseAndWriterNicknameOrderByWrittenAtDesc(nickname: String, pageable: Pageable? = null): List<PostEntity>

    fun findAllByDeletedIsFalseAndTagsTagOrderByWrittenAtDesc(tag: String, pageable: Pageable? = null): List<PostEntity>

    @Query("""
        select p
        from PostEntity p
        where cast(p.writtenAt as date) = :date and p.deleted = false
        order by size(p.recommends)
        limit 10
    """)
    fun findTop10ByDate(date: LocalDate): List<PostEntity>

    @Query("""
        select p
        from PostEntity p
        where year(p.writtenAt) = year(:data) and month(p.writtenAt) = month(:data) and p.deleted = false
        order by size(p.recommends)
        limit 10
    """)
    fun findTop10ByMonth(data: LocalDate): List<PostEntity>

    @Query("""
        select p
        from PostEntity p
        where p.deleted = false and p.id not in :excludePostIds
        order by function('rand')
        limit 1
    """)
    fun getRandomPost(excludePostIds: Iterable<Int> = emptySet()): PostEntity?

    @Query("""
        select p
        from PostEntity p
        where p.deleted = false and p.id not in :excludePostIds
        order by function('rand')
        limit :n
    """)
    fun getNRandomPost(excludePostIds: Iterable<Int> = emptySet(), n: Int = 1): List<PostEntity>
}