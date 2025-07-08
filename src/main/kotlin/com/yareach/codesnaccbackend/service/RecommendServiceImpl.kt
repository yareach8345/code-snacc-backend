package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.post.UpdateRecommendResponse
import com.yareach.codesnaccbackend.entity.RecommendEntity
import com.yareach.codesnaccbackend.entity.RecommendEntityId
import com.yareach.codesnaccbackend.exception.PostNotFoundException
import com.yareach.codesnaccbackend.exception.UserNotFoundException
import com.yareach.codesnaccbackend.extensions.findOrThrow
import com.yareach.codesnaccbackend.repository.PostRepository
import com.yareach.codesnaccbackend.repository.RecommendRepository
import com.yareach.codesnaccbackend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class RecommendServiceImpl (
    val recommendRepository: RecommendRepository,
    val userRepository: UserRepository,
    val postRepository: PostRepository,
): RecommendService {
    override fun recommendPost(postId: Int, userId: String): UpdateRecommendResponse {
        val recomment = RecommendEntity(
            RecommendEntityId(
                user = userRepository.findOrThrow(userId) { throw UserNotFoundException(userId) },
                post = postRepository.findOrThrow(postId) { throw PostNotFoundException(postId) }
            )
        )
        recommendRepository.save(recomment)

        val recommendCnt = recommendRepository.countByPostId(postId)

        return UpdateRecommendResponse(
            didIRecommended = true,
            recommendCnt
        )
    }

    @Transactional
    override fun cancelRecommendPost(postId: Int, userId: String): UpdateRecommendResponse {
        val recommendId = RecommendEntityId(
            user = userRepository.findOrThrow(userId) { throw UserNotFoundException(userId) },
            post = postRepository.findOrThrow(postId) { throw PostNotFoundException(postId) }
        )

        recommendRepository.deleteById(recommendId)

        val recommendCnt = recommendRepository.countByPostId(postId)

        return UpdateRecommendResponse(
            didIRecommended = false,
            recommendCnt
        )
    }
}