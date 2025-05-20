package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.PostEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PostRepository: JpaRepository<PostEntity, Int> {
    fun findAllByDeletedIsFalseOrderByWrittenAtDesc(pageable: Pageable): List<PostEntity>

    fun findAllByDeletedIsFalseAndTitleContainingOrderByWrittenAtDesc(title: String, pageable: Pageable? = null): List<PostEntity>

    fun findAllByDeletedIsFalseAndWriterIdOrderByWrittenAtDesc(userId: String, pageable: Pageable? = null): List<PostEntity>

    fun findAllByDeletedIsFalseAndTagsTagOrderByWrittenAtDesc(tag: String, pageable: Pageable? = null): List<PostEntity>

    fun findAllByDeletedIsFalseAndLanguageOrderByWrittenAtDesc(language: String, pageable: Pageable? = null): List<PostEntity>

    @Query("""
        select p
        from PostEntity p
        left join p.tags t
        where p.deleted = false and 
            (:title is null or p.title like concat('%', :title, '%')) and 
            (:userId is null or p.writer.id = :userId) and
            (:tagSize = 0 or t.tag in (:tags)) and
            (:language is null or p.language = :language)
        group by p.id
        having :tagSize = 0 or count(distinct t.tag) >= :tagSize
        """
    )
    fun findPostsBySearchCondition(title: String? = null, userId: String? = null, tags: Collection<String> = emptySet(), language: String? = null, tagSize: Int = tags.size, pageable: Pageable = PageRequest.of(0, 10, Sort.by("writtenAt").descending())): List<PostEntity>

    @Query("""
        select count(*)
        from (
            select p.post_id
            from posts p
            left join post_tags pt on pt.post_id = p.post_id
            left join tags t on pt.tag = t.tag
            where p.deleted = false and
                (:title is null or p.title like concat('%', :title, '%')) and 
                (:userId is null or p.user_id = :userId) and
                (:tagSize = 0 or t.tag in (:tags)) and
                (:language is null or p.lang = :language)
            group by p.post_id
            having :tagSize = 0 or count(distinct t.tag) >= :tagSize
            order by p.written_at desc
        ) as search_result
    """,
        nativeQuery = true
    )
    fun countBySearchCondition(title: String? = null, userId: String? = null, tags: Collection<String> = emptySet(), language: String? = null, tagSize: Int = tags.size): Int


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

    fun countByDeletedIsFalse(): Int
}