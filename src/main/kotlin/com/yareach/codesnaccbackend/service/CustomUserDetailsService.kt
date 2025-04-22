package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.exception.UserNotFoundException
import com.yareach.codesnaccbackend.extensions.findOrThrow
import com.yareach.codesnaccbackend.extensions.toCustomUserDetails
import com.yareach.codesnaccbackend.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
        if (username == null)
            throw IllegalArgumentException("사용자 이름은 필수 항목입니다.")
        else {
            return userRepository
                .findOrThrow(username) { throw UserNotFoundException(username) }
                .toCustomUserDetails()
        }
    }
}