package com.yareach.codesnaccbackend.repository

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql

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
        assert(post != null)
        assertEquals(1, post?.id)
        assertEquals("test-post1", post?.title)
        assertEquals(2, post?.tags?.size)
        assertEquals(setOf("test-tag1", "test-tag2"), post?.tags?.map { it.tag }?.toSet())
        assertEquals(4, post?.recommends?.size)
        assertEquals(setOf("test-user1", "test-user2", "admin1", "black-sheep"), post?.recommends?.map{ it.id }?.toSet())
    }
}