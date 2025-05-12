package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto

interface PostService {

    //functions for reading
    fun getNPosts(n: Int, page: Int): List<PostInfoResponseDto>

    fun getPostById(id: Int): PostInfoResponseDto

    fun getRandomPost(exclude: List<Int>?): PostInfoResponseDto
}