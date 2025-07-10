package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.tag.TagDto
import com.yareach.codesnaccbackend.service.TagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("tags")
class TagController(
    val tagService: TagService
) {
    @GetMapping
    fun getAllTags(): ResponseEntity<TagDto> {
        val tagsDto = tagService.getTags()
        return ResponseEntity.ok(tagsDto)
    }
}