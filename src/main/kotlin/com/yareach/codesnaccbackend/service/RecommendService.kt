package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.recommend.UpdateRecommendResponse

interface RecommendService {
    fun recommendPost(postId: Int, userId: String): UpdateRecommendResponse

    fun cancelRecommendPost(postId: Int, userId: String): UpdateRecommendResponse

    fun getRecommendCount(postId: Int): Long
}