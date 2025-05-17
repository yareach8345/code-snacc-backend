package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.TagEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository: JpaRepository<TagEntity, String> {
    fun findByTagIn(tags: Collection<String>): MutableSet<TagEntity>
}