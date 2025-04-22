package com.yareach.codesnaccbackend.repository

import com.yareach.codesnaccbackend.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CrudRepository<UserEntity, String>