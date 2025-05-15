package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.PostEntity
import com.yareach.codesnaccbackend.entity.TagEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PostRepository: JpaRepository<PostEntity, Int> {
    fun findAllByDeletedIsFalse(pageable: Pageable): List<PostEntity>

    @Query("""
        select p 
        from PostEntity p 
        where p.writer.id = :userId and p.deleted = false
        order by p.writtenAt desc
    """)
    fun findByWriterId(userId: String): List<PostEntity>

    @Query("""
        select p
        from PostEntity p
        where p.writer.nickname = :nicckname and p.deleted = false
        order by p.writtenAt desc
    """)
    fun findByWriterNickname(nickname: String): List<PostEntity>

    @Query("""
        select p 
        from PostEntity p
        where :tag member of p.tags and p.deleted = false
        order by p.writtenAt desc
    """)
    fun findByTag(tag: TagEntity): List<PostEntity>

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