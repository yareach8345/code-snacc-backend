package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.extensions.toDto
import com.yareach.codesnaccbackend.repository.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull

@Service
class PostServiceImpl(
    val postRepository: PostRepository
): PostService {
    override fun getNPosts(
        n: Int,
        page: Int,
        userId: String?
    ): List<PostInfoResponseDto> =
        PageRequest.of(page, n, Sort.by("writtenAt").descending()).let { pageable ->
            postRepository
                .findAllByDeletedIsFalse(pageable = pageable)
                .map{ it.toDto(userId) }
        }

    override fun getPostById(
        id: Int,
        userId: String?
    ): PostInfoResponseDto? =
        postRepository
            .findByIdOrNull(id)
            ?.toDto(userId)

    override fun getRandomPost(
        exclude: Iterable<Int>?,
        userId: String?
    ): PostInfoResponseDto? =
        postRepository
            .getRandomPost(exclude ?: emptySet())
            ?.toDto(userId)

    override fun getNRandomPost(
        exclude: Iterable<Int>?,
        n: Int,
        userId: String?
    ): List<PostInfoResponseDto> =
        postRepository
            .getNRandomPost(exclude ?: emptySet(), n)
            .map { it.toDto(userId) }
}