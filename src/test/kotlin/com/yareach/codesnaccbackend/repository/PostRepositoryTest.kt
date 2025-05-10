package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.TagEntity
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDate
import kotlin.test.assertNotNull

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
        val posts = postRepository.findByWriterId("test-user1")
        assertNotNull(posts)
        assertEquals(setOf("test-user1"), posts.map { it.writer.id }.toSet())
        assertEquals(posts.sortedByDescending { it.writtenAt }, posts)
    }

    @Test
    @DisplayName("tag로 post 조회 테스트")
    fun findTestByTag() {
        val tagEntity = TagEntity(tag = "test-tag1")
        val posts = postRepository.findByTag(tagEntity)
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
}