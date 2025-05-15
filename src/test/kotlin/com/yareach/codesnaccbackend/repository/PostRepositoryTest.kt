package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.TagEntity
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDate
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DataJpaTest
@Sql(scripts = [
    "classpath:db/scripts/init-users.sql",
    "classpath:db/scripts/init-posts.sql"
])
@Transactional
class PostRepositoryTest {
    @Autowired
    lateinit var postRepository: PostRepository

    @Test
    @DisplayName("기본적은 post 조회 테스트")
    fun findTest() {
        val post = postRepository.findByIdOrNull(1)
        assertNotNull(post)
        assertEquals(1, post.id)
        assertEquals("test-post1", post.title)
        assertEquals(2, post.tags.size)
        assertEquals(setOf("test-tag1", "test-tag2"), post.tags.map { it.tag }.toSet())
        assertEquals(4, post.recommends.size)
        assertEquals(setOf("test-user1", "test-user2", "admin1", "black-sheep"), post.recommends.map{ it.id }.toSet())
    }

    @Test
    @DisplayName("작성자의 id로 post 조회 테스트")
    fun findTestByAuthorId() {
        val posts = postRepository.findAllByDeletedIsFalseAndWriterIdOrderByWrittenAtDesc("test-user1")
        assertNotNull(posts)
        assertEquals(setOf("test-user1"), posts.map { it.writer.id }.toSet())
        assertEquals(posts.sortedByDescending { it.writtenAt }, posts)
    }

    @Test
    @DisplayName("tag로 post 조회 테스트")
    fun findTestByTag() {
        val tagEntity = TagEntity(tag = "test-tag1")
        val posts = postRepository.findAllByDeletedIsFalseAndTagsTagOrderByWrittenAtDesc(tagEntity.tag)
        assertNotNull(posts)
        assertEquals(2, posts.size)
        assertEquals(setOf(true), posts.map { it.tags.map{ it.tag }.contains(tagEntity.tag) }.toSet())
        assertEquals(posts.sortedByDescending { it.writtenAt }, posts)
    }

    @Test
    @DisplayName("특정 날자의 Top 10 개시글 구하기")
    fun findTop10ByDate() {
        val posts = postRepository.findTop10ByDate(LocalDate.of(2025, 5, 10))
        println(posts)
        assertNotNull(posts)
    }

    @Test
    @DisplayName("특정 달의 Top 10 개시글 구하기")
    fun findTop10ByMonth() {
        val posts = postRepository.findTop10ByMonth(LocalDate.of(2025, 5, 10))
        println(posts)
        assertNotNull(posts)
    }

    @Test
    @DisplayName("N개의 최신 개시글 구하기")
    fun getWithPaging() {
        val pageable = PageRequest.of(0, 3, Sort.by("writtenAt").descending())
        val posts = postRepository.findAll(pageable).toList()
        assertNotNull(posts)
        assertEquals(3, posts.size)
        assertEquals(posts.sortedByDescending { it.writtenAt }, posts)

        val pageable2 = PageRequest.of(1, 3, Sort.by("writtenAt").descending())
        val posts2 = postRepository.findAll(pageable2).toList()
        println(posts2)
        assertNotNull(posts2)
        assertEquals(2, posts2.size)
        assertEquals(posts2.sortedByDescending { it.writtenAt }, posts2)
    }

    @Test
    @DisplayName("삭제 되지 않은 N개의 최신 개시글 구하기")
    fun getWithPagingNotDeleted() {
        val pageable = PageRequest.of(0, 3, Sort.by("writtenAt").descending())
        val posts = postRepository.findAllByDeletedIsFalseOrderByWrittenAtDesc(pageable).toList()
        assertNotNull(posts)
        assertEquals(3, posts.size)
        assertEquals(posts.sortedByDescending { it.writtenAt }, posts)

        val pageable2 = PageRequest.of(1, 3, Sort.by("writtenAt").descending())
        val posts2 = postRepository.findAllByDeletedIsFalseOrderByWrittenAtDesc(pageable2).toList()
        println(posts2)
        assertNotNull(posts2)
        assertEquals(1, posts2.size)
        assertEquals(posts2.sortedByDescending { it.writtenAt }, posts2)
    }

    @Test
    @DisplayName("랜덤 개시글 조회")
    fun getRandomPost() {
        val post = postRepository.getRandomPost()
        assertNotNull(post)
    }

    @Test
    @DisplayName("특정 id 제외하고 랜덤 개시글 조회")
    fun getRandomPostExcept() {
        val post = postRepository.getRandomPost(listOf(1, 2, 3))
        assertNotNull(post)
        assertEquals(post.id, 4)
    }

    @Test
    @DisplayName("남은 게시글이 더이상 없을때 Null이 반환됨")
    fun getRandomButNot() {
        val post = postRepository.getRandomPost(listOf(1, 2, 3, 4))
        assertNull(post)
    }
}