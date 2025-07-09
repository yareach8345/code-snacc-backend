package com.yareach.codesnaccbackend.dto.recommend

data class UpdateRecommendResponse (
    val didIRecommended: Boolean,
    val recommendCnt: Long,
)