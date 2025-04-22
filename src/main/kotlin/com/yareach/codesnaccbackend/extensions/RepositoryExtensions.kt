package com.yareach.codesnaccbackend.extensions

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

fun <T, ID> JpaRepository<T, ID>.findOrThrow(id: ID, exception: () -> RuntimeException): T {
    return findByIdOrNull(id) ?: throw exception()
}