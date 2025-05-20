package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.PostEntity
import com.yareach.codesnaccbackend.entity.TagEntity
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDate
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DataJpaTest
@Sql(scripts = [
    "classpath:db/scripts/init-users.sql",
    "classpath:db/scripts/init-posts.sql",
    "classpath:db/scripts/init-comments.sql"
])
@ActiveProfiles("test")
@Transactional
class PostRepositoryTest {
    @Autowired
    lateinit var postRepository: PostRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var tagRepository: TagRepository

    @Test
    @DisplayName("기본적은 post 조회 테스트")
    fun findTest() {
        val post = postRepository.findByIdOrNull(0)
        assertNotNull(post)
        assertEquals(0, post.id)
        assertEquals("test-post1", post.title)
        assertEquals(2, post.tags.size)
        assertEquals(setOf("test-tag1", "test-tag2"), post.tags.map { it.tag }.toSet())
        assertEquals(4, post.recommends.size)
        assertEquals(setOf("test-user1", "test-user2", "admin1", "black-sheep"), post.recommends.map{ it.id }.toSet())
        assertEquals(3, post.comments.size)
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
        val post = postRepository.getRandomPost(listOf(0, 1, 2))
        assertNotNull(post)
        assertEquals(post.id, 3)
    }

    @Test
    @DisplayName("남은 게시글이 더이상 없을때 Null이 반환됨")
    fun getRandomButNot() {
        val post = postRepository.getRandomPost(listOf(0, 1, 2, 3))
        assertNull(post)
    }

    @Test
    @DisplayName("save post")
    @Transactional
    fun savePost() {
        val user = userRepository.findById("test-user1").get()
        val tags = tagRepository.findByTagIn(setOf("test-tag1", "test-tag2")).toMutableSet()

        val post = postRepository.save(
            PostEntity(
                title = "test-post-save",
                content = "test-content-save",
                writer = user,
                tags = tags,
                code = "Java Code",
                language = "Java"
            )
        )

        val savedPost = postRepository.findByIdOrNull(post.id)

        assertNotNull(savedPost)
        assertEquals("test-post-save", savedPost.title)
        assertEquals("test-content-save", savedPost.content)
        assertEquals("test-user1", savedPost.writer.id)
        assertFalse(savedPost.tags.isEmpty())
        assertEquals(2, savedPost.tags.size)
        assertTrue { savedPost.tags.map { it.tag }.let{
           "test-tag1" in it && "test-tag2" in it
        } }
        assertEquals("Java Code", savedPost.code)
        assertEquals("Java", savedPost.language)
        assertTrue(savedPost.recommends.isEmpty())
        assertTrue(savedPost.comments.isEmpty())
    }

    @Test
    @DisplayName("search by 테스트 1 - 별다른 조건이 없을 때 모든 게시글 반환")
    fun findPostsBySearchCondition() {
        val countOfAllOfPosts = postRepository.countByDeletedIsFalse()
        val searchResult = postRepository.findPostsBySearchCondition()
        val numberOfPost = postRepository.countBySearchCondition()

        println(searchResult)
        assertEquals(countOfAllOfPosts, numberOfPost)
        assertEquals(4, searchResult.size)
        assertEquals(searchResult.sortedByDescending { it.writtenAt }, searchResult)
    }

    @Test
    @DisplayName("search by 테스트 2 - pageable")
    fun findPostsBySearchConditionWithPaging() {
        val countOfAllOfPosts = postRepository.countByDeletedIsFalse()
        val pageable = PageRequest.of(0, 3, Sort.by("writtenAt").descending())

        val searchResult = postRepository.findPostsBySearchCondition(pageable = pageable)
        val numberOfPost = postRepository.countBySearchCondition()

        assertEquals(countOfAllOfPosts, numberOfPost)
        assertEquals(3, searchResult.size)
    }

    @Test
    @DisplayName("search by 테스트 3 - title")
    fun findPostsBySearchConditionWithTitle() {
        val searchResult = postRepository.findPostsBySearchCondition(title = "test-post1")

        assertTrue(searchResult.all { it.title == "test-post1" })
    }

    @Test
    @DisplayName("search by 테스트 4 - tag (단일)")
    fun findPostsBySearchConditionWithTag() {
        val searchResult = postRepository.findPostsBySearchCondition(tags = listOf("test-tag1"))

        assertTrue( searchResult.all { it.tags.map{ it.tag }.contains("test-tag1") })
    }

    @Test
    @DisplayName("search by 테스트 4 - tag (복수)")
    fun findPostsBySearchConditionWithPloralTag() {
        val searchResult = postRepository.findPostsBySearchCondition(tags = listOf("test-tag1", "test-tag2"))

        assertTrue(searchResult.all { it.tags.map{ it.tag }.let{ it.contains("test-tag1") && it.contains("test-tag2") } })
    }

    @Test
    @DisplayName("search by 테스트 5 - userId")
    fun findPostsBySearchConditionWithUserId() {
        val searchResult = postRepository.findPostsBySearchCondition(userId = "test-user1")

        assertTrue(searchResult.all { it.writer.id == "test-user1" })
    }

    @Test
    @DisplayName("search by 테스트 6 - language")
    fun findPostsBySearchConditionWithLanguage() {
        val searchResult = postRepository.findPostsBySearchCondition(language = "<LANG>")

        assertTrue(searchResult.all { it.language == "<LANG>" })
    }

    @Test
    @DisplayName("search by 테스트 7 - 복합")
    fun findPostsBySearchConditionWithCode() {
        val searchResult = postRepository.findPostsBySearchCondition(
            userId = "test-user1",
            title = "test-post",
            tags = listOf("test-tag1", "test-tag2"),
        )

        assertTrue {
            searchResult.all { it.writer.id == "test-user1" }
        }

        assertTrue {
            searchResult.all { it.title.startsWith("test-post") }
        }

        assertTrue {
            searchResult.all { it.tags.map{ it.tag }.let{ it.contains("test-tag1") && it.contains("test-tag2") } }
        }
    }

    @Test
    @DisplayName("갯수 가져오기")
    fun countBySearchCondition() {
        val count = postRepository.countBySearchCondition()
        assertEquals(4, count)
    }
}