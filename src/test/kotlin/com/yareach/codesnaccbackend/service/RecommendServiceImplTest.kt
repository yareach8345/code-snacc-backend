package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.entity.PostEntity
import com.yareach.codesnaccbackend.entity.RecommendEntity
import com.yareach.codesnaccbackend.entity.RecommendEntityId
import com.yareach.codesnaccbackend.entity.UserEntity
import com.yareach.codesnaccbackend.exception.PostNotFoundException
import com.yareach.codesnaccbackend.exception.UserNotFoundException
import com.yareach.codesnaccbackend.repository.PostRepository
import com.yareach.codesnaccbackend.repository.RecommendRepository
import com.yareach.codesnaccbackend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertThrows
import java.util.Optional
import kotlin.test.assertEquals

class RecommendServiceImplTest {
    val recommendRepositoryMock = mockk<RecommendRepository>()
    val userRepositoryMock = mockk<UserRepository>()
    val postRepositoryMock = mockk<PostRepository>()

    val recommendService: RecommendServiceImpl = RecommendServiceImpl(
        recommendRepositoryMock,
        userRepositoryMock,
        postRepositoryMock
    )

    val testUser = UserEntity(
        id = "test-user",
        password = "<PASSWORD>"
    )

    val testPost1 = PostEntity(
        id = 0,
        writer = testUser,
        title = "<TEST POST>",
        code = "<CODE>",
        language = "<LANGUAGE>",
        content = "<CONTENT>",
        deleted = false,
        tags = mutableSetOf(),
        recommends = mutableSetOf(),
    )

    val testPost2 = PostEntity(
        id = 1,
        writer = testUser,
        title = "<TEST POST>",
        code = "<CODE>",
        language = "<LANGUAGE>",
        content = "<CONTENT>",
        deleted = false,
        tags = mutableSetOf(),
        recommends = mutableSetOf( testUser ),
    )

    @BeforeEach
    fun initUserRepository() {
        val userIdSlot = slot<String>()

        every { userRepositoryMock.findById(capture(userIdSlot)) }
            .answers{
                if(userIdSlot.captured == "test-user")
                    Optional.of(testUser)
                else
                    Optional.empty()
            }
    }

    @BeforeEach
    fun initPostRepository() {
        val postIdSlot = slot<Int>()

        every { postRepositoryMock.findById(capture(postIdSlot)) }
            .answers {
                when(postIdSlot.captured) {
                    0 -> Optional.of(testPost1)
                    1 -> Optional.of(testPost2)
                    else -> Optional.empty()
                }
            }
    }

    @BeforeEach
    fun initRecommendRepository() {
        val postIdSlot = slot<Int>()
        val recommendEntitySlot = slot<RecommendEntity>()
        val recommendIdSlot = slot<RecommendEntityId>()

        val recommendMap = mutableMapOf<Int, Long>()
        recommendMap[0] = 0
        recommendMap[1] = 1

        every { recommendRepositoryMock.countByPostId(capture(postIdSlot)) }
            .answers { recommendMap[postIdSlot.captured] ?: 0 }


        every { recommendRepositoryMock.save(capture(recommendEntitySlot)) }
            .answers {
                val postId = recommendEntitySlot.captured.recommendId.post.id!!
                recommendMap[postId] = (recommendMap[postId] ?: 0) + 1
                recommendEntitySlot.captured
            }

        every { recommendRepositoryMock.deleteById(capture(recommendIdSlot)) }
            .answers{
                val postId = recommendIdSlot.captured.post.id!!
                recommendMap[postId] = (recommendMap[postId] ?: 0) - 1
            }
    }

    @Test
    @DisplayName("게시글을 추천하면 post의 recommend가 1 증가함")
    fun recommendTest() {
        val recommendCountBeforeRecommend = recommendService.getRecommendCount(1)
        val recommendResult = recommendService.recommendPost(1, testUser.id)
        val recommendCountAfterRecommend = recommendService.getRecommendCount(1)

        assertEquals(1, recommendCountBeforeRecommend)
        assertEquals(2, recommendCountAfterRecommend)
        assertEquals(1, recommendCountAfterRecommend - recommendCountBeforeRecommend)

        assertEquals(true, recommendResult.didIRecommended)
        assertEquals(2, recommendResult.recommendCnt)
    }

    @Test
    @DisplayName("없는 아이디로 요청하면 실패함")
    fun recommendTestWithNotExistUser() {
        val recommendCountBeforeRecommend = recommendService.getRecommendCount(1)
        assertThrows(UserNotFoundException::class.java){ recommendService.recommendPost(1, "Id-not-exists") }
        val recommendCountAfterRecommend = recommendService.getRecommendCount(1)

        assertEquals(1, recommendCountBeforeRecommend)
        assertEquals(1, recommendCountAfterRecommend)
        assertEquals(0, recommendCountAfterRecommend - recommendCountBeforeRecommend)
    }

    @Test
    @DisplayName("존재하지 않는 개시글 추천시 실패")
    fun recommendToNotExistPostTest() {
        assertThrows(PostNotFoundException::class.java){ recommendService.recommendPost(-1, testUser.id) }
    }

    @Test
    @DisplayName("게시글의 추천을 취소 하면 post의 recommend가 1 감소함")
    fun cancelRecommendTest() {
        val recommendCountBeforeRecommend = recommendService.getRecommendCount(1)
        val recommendResult = recommendService.cancelRecommendPost(1, testUser.id)
        val recommendCountAfterRecommend = recommendService.getRecommendCount(1)

        assertEquals(1, recommendCountBeforeRecommend)
        assertEquals(0, recommendCountAfterRecommend)
        assertEquals(-1, recommendCountAfterRecommend - recommendCountBeforeRecommend)

        assertEquals(false, recommendResult.didIRecommended)
        assertEquals(0, recommendResult.recommendCnt)
    }

    @Test
    @DisplayName("없는 아이디로 추천 삭제 요청하면 실패함")
    fun cancelRecommendTestWithNotExistUser() {
        val recommendCountBeforeRecommend = recommendService.getRecommendCount(1)
        assertThrows(UserNotFoundException::class.java){ recommendService.cancelRecommendPost(1, "Id-not-exists") }
        val recommendCountAfterRecommend = recommendService.getRecommendCount(1)

        assertEquals(1, recommendCountBeforeRecommend)
        assertEquals(1, recommendCountAfterRecommend)
        assertEquals(0, recommendCountAfterRecommend - recommendCountBeforeRecommend)
    }

    @Test
    @DisplayName("존재하지 않는 개시글 추천 취소시 실패")
    fun cancelRecommendToNotExistPostTest() {
        assertThrows(PostNotFoundException::class.java){ recommendService.cancelRecommendPost(-1, testUser.id) }
    }
}