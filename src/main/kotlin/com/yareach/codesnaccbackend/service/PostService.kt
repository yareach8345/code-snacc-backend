package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto

interface PostService {

    //functions for reading
    fun getNPosts(n: Int, page: Int, userId: String? = null): List<PostInfoResponseDto>

    fun getPostById(id: Int, userId: String? = null): PostInfoResponseDto?

    fun getRandomPost(exclude: Iterable<Int>? = null, userId: String? = null): PostInfoResponseDto?
}