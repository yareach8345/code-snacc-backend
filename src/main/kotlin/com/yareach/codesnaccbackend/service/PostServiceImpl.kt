package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.dto.post.PostSearchDto
import com.yareach.codesnaccbackend.dto.post.SearchPostResultDto
import com.yareach.codesnaccbackend.dto.post.PostUploadDto
import com.yareach.codesnaccbackend.entity.PostEntity
import com.yareach.codesnaccbackend.exception.UserNotFoundException
import com.yareach.codesnaccbackend.extensions.findOrThrow
import com.yareach.codesnaccbackend.extensions.toDto
import com.yareach.codesnaccbackend.repository.PostRepository
import com.yareach.codesnaccbackend.repository.TagRepository
import com.yareach.codesnaccbackend.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull

@Service
class PostServiceImpl(
    val postRepository: PostRepository,
    val userRepository: UserRepository,
    val tagRepository: TagRepository
): PostService {
    override fun searchPosts(pageSize: Int, page: Int, userId: String?, searchDto: PostSearchDto?): SearchPostResultDto = SearchPostResultDto(
        posts = postRepository
            .findPostsBySearchCondition(
                title = searchDto?.title,
                userId = searchDto?.writerId,
                tags = searchDto?.tags ?: emptySet(),
                language = searchDto?.language,
                pageable = PageRequest.of(page, pageSize, Sort.by("writtenAt").descending())
            ).map { it.toDto(userId) },
        numberOfPosts = postRepository
            .countBySearchCondition(
                title=searchDto?.title,
                userId = searchDto?.writerId,
                tags = searchDto?.tags ?: emptySet(),
                language = searchDto?.language,
            ),
        pageNumber = page,
        pageSize = pageSize
    )

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

    override fun uploadPost(postUploadDto: PostUploadDto): Int {
        val userEntity = userRepository.findOrThrow(postUploadDto.writerId) { UserNotFoundException(postUploadDto.writerId) }
        val tagEntities = tagRepository.findByTagIn(postUploadDto.tags)
        val postEntity = PostEntity (
            writer = userEntity,
            title = postUploadDto.title,
            code = postUploadDto.code,
            language = postUploadDto.language,
            content = postUploadDto.content,
            tags = tagEntities,
        )

        val uploadedPost = postRepository.save(postEntity)

        return uploadedPost.id!!
    }
}