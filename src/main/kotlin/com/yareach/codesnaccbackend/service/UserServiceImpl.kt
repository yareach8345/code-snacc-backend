package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.user.UserInfoDto
import com.yareach.codesnaccbackend.dto.user.UserJoinDto
import com.yareach.codesnaccbackend.entity.UserEntity
import com.yareach.codesnaccbackend.exception.UserIdDuplicateException
import com.yareach.codesnaccbackend.exception.UserNotFoundException
import com.yareach.codesnaccbackend.repository.UserRepository
import com.yareach.codesnaccbackend.extensions.findOrThrow
import com.yareach.codesnaccbackend.extensions.toUserInfoDto
import jakarta.transaction.Transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val bCryptPasswordEncoder: BCryptPasswordEncoder
): UserService {

    override fun join(userJoinDto: UserJoinDto) {
        // 아이디 중복 확인
        if (userRepository.existsById(userJoinDto.userId))
            throw UserIdDuplicateException(userJoinDto.userId)

        val newUser = UserEntity(
            id = userJoinDto.userId,
            password = bCryptPasswordEncoder.encode(userJoinDto.password),
            nickName = userJoinDto.nickName,
        )

        userRepository.save(newUser)
    }

    override fun getUserInfo(id: String): UserInfoDto =
        userRepository
            .findOrThrow(id) { UserNotFoundException(id) }
            .toUserInfoDto()

    @Transactional
    override fun quit(id: String) =
        userRepository
            .findOrThrow(id) { UserNotFoundException(id) }
            .quit()

    override fun isIdExists(id: String): Boolean =
        userRepository
            .existsById(id)
}