package com.yareach.codesnaccbackend.dto.post

data class UpdateRecommendResponse (
    val didIRecommended: Boolean,
    val recommendCnt: Long,
)