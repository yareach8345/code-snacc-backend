package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.PostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository: JpaRepository<PostEntity, Int>