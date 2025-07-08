package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.post.UpdateRecommendResponse

interface RecommendService {
    fun recommendPost(postId: Int, userId: String): UpdateRecommendResponse

    fun cancelRecommendPost(postId: Int, userId: String): UpdateRecommendResponse
}