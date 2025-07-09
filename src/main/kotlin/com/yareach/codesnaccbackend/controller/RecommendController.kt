package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.recommend.RecommendDto
import com.yareach.codesnaccbackend.dto.recommend.UpdateRecommendResponse
import com.yareach.codesnaccbackend.exception.AccessDeniedException
import com.yareach.codesnaccbackend.service.RecommendService
import com.yareach.codesnaccbackend.util.getUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts/{postId}/recommend")
class RecommendController(
    private val recommendService: RecommendService,
) {
    @PostMapping()
    fun updateRecommend(@PathVariable postId: Int): ResponseEntity<UpdateRecommendResponse> {
        val userId = getUserId(SecurityContextHolder.getContext().authentication) ?: throw AccessDeniedException("로그인 이후에만 사용가능한 기능입니다.")
        val result = recommendService.recommendPost(postId, userId)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping()
    fun cancelRecommend(@PathVariable postId: Int): ResponseEntity<UpdateRecommendResponse> {
        val userId = getUserId(SecurityContextHolder.getContext().authentication) ?: throw AccessDeniedException("로그인 이후에만 사용가능한 기능입니다.")
        val result = recommendService.cancelRecommendPost(postId, userId)
        return ResponseEntity.ok(result)
    }

    @GetMapping()
    fun getRecommendCnt(@PathVariable postId: Int): ResponseEntity<RecommendDto> {
        val recommendCount = recommendService.getRecommendCount(postId)
        val result = RecommendDto(
            recommendCount
        )
        return ResponseEntity.ok(result)
    }
}