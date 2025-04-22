package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserEntity, String>