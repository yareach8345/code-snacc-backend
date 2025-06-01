package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.dto.post.PostSearchDto
import com.yareach.codesnaccbackend.dto.post.PostUpdateDto
import com.yareach.codesnaccbackend.dto.post.SearchPostResultDto
import com.yareach.codesnaccbackend.dto.post.PostUploadDto

interface PostService {

    //functions for reading
    fun searchPosts(pageSize: Int, page: Int = 0, userId: String? = null, searchDto: PostSearchDto? = null): SearchPostResultDto

    fun getPostById(id: Int, userId: String? = null): PostInfoResponseDto?

    fun getRandomPost(exclude: Iterable<Int>? = null, userId: String? = null): PostInfoResponseDto?

    fun getNRandomPost(exclude: Iterable<Int>? = null, n: Int = 1, userId: String? = null): List<PostInfoResponseDto>

    fun uploadPost(postUploadDto: PostUploadDto): Int

    fun deletePost(postId: Int, userId: String? = null)

    fun updatePost(postId: Int, postUpdateDto: PostUpdateDto, userId: String? = null)
}