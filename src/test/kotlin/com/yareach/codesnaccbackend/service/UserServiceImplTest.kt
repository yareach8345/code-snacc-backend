package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.user.UserJoinDto
import com.yareach.codesnaccbackend.entity.UserEntity
import com.yareach.codesnaccbackend.entity.UserRole
import com.yareach.codesnaccbackend.exception.UserIdDuplicateException
import com.yareach.codesnaccbackend.exception.UserNotFoundException
import com.yareach.codesnaccbackend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@ExtendWith(MockitoExtension::class)
class UserServiceImplTest{
    val userRepository = mockk<UserRepository>()

    val bCryptPasswordEncoder = mockk<BCryptPasswordEncoder>()

    val userService = UserServiceImpl(userRepository, bCryptPasswordEncoder)

    @Test
    @DisplayName("회원가입시 Repository에 적절한 Entity를 만들어 전달합니다.")
    fun join() {
        val userJoinDto = UserJoinDto(
            id = "testId",
            password = "PASSWORD",
            nickName = "testNickName"
        )

        //Repository 모킹 설정
        val captureUser = slot<UserEntity>()
        every { userRepository.existsById(userJoinDto.id) }.returns(false)
        every { userRepository.save(capture(captureUser)) } answers { captureUser.captured }

        //Encoder 모킹 설정
        val capturePassword = slot<String>()
        every { bCryptPasswordEncoder.encode(capture(capturePassword)) }.answers { "SOMETHING ENCODED VALUE" }

        userService.join(userJoinDto)

        //id와 nickname은 본래 넣은 값으로 채워져야 함
        assertEquals(userJoinDto.id, captureUser.captured.id)
        assertEquals(userJoinDto.nickName, captureUser.captured.nickName)
        //password는 그대로 들어가면 안되고 암호화 시켜야 함
        assertNotEquals(userJoinDto.password, captureUser.captured.password)
        assertEquals("SOMETHING ENCODED VALUE", captureUser.captured.password)
        //나머지 필드는 기본값으로 채워야 함
        assertEquals(0, captureUser.captured.warnCnt)
        assertEquals(false, captureUser.captured.banned)
        assertEquals(false, captureUser.captured.quit)
        assertEquals(UserRole.USER, captureUser.captured.role)
    }

    @Test
    @DisplayName("회원가입시 user id가 중복되는 경우")
    fun joinDuplicateUserId() {
        val userJoinDto = UserJoinDto(
            id = "testId",
            password = "PASSWORD",
            nickName = "testNickName"
        )

        every { userRepository.existsById(userJoinDto.id) }.returns(true)

        assertThrows(UserIdDuplicateException::class.java) {
            userService.join(userJoinDto)
        }
    }

    @Test
    @DisplayName("유저 조회 성공하는 경우")
    fun getUserInfo() {
        val testUserEntity = UserEntity(
            id = "testId",
            password = "<PASSWORD>",
            nickName = "testNickName",
            role = UserRole.USER,
            banned = false,
            quit = false,
            warnCnt = 0
        )

        every { userRepository.findByIdOrNull("testId") }.returns(testUserEntity)

        val userInfoDto = userService.getUserInfo("testId")

        assertEquals(testUserEntity.id, userInfoDto.id)
        assertEquals(testUserEntity.nickName, userInfoDto.nickName)
    }

    @Test
    @DisplayName("id에 해당하는 유저가 없어 유저 조회가 실패하는 경우")
    fun getUserInfoNotFoundUser() {
        every { userRepository.findByIdOrNull("testId") }.returns(null)

        assertThrows(UserNotFoundException::class.java){
            userService.getUserInfo("testId")
        }
    }

    @Test
    @DisplayName("회원 탈퇴에 성공하는 케이스")
    fun quit() {
        val testUserEntity = UserEntity(
            id = "testId",
            password = "<PASSWORD>",
            nickName = "testNickName",
            role = UserRole.USER,
            banned = false,
            quit = false,
            warnCnt = 0
        )

        every { userRepository.findByIdOrNull("testId") }.returns(testUserEntity)

        userService.quit("testId")

        assertTrue(testUserEntity.quit)
    }

    @Test
    @DisplayName("id에 해당하는 유저가 없어 탈퇴에 실패")
    fun quitNotFoundUser() {
        every { userRepository.findByIdOrNull("testId") }.returns(null)

        assertThrows(UserNotFoundException::class.java){
            userService.getUserInfo("testId")
        }
    }

    @Test
    @DisplayName("id가 이미 존재할 경우 isIdExists는 True 반환")
    fun isIdExistsWhenExistId() {
        val id = "existingId"
        every { userRepository.existsById(id) } returns true

        val result = userService.isIdExists(id)

        assertTrue(result)
    }

    @Test
    @DisplayName("id가 존재하지 않을 경우 isIdExists는 False 반환")
    fun isIdExistsWhenNonExistingId() {
        val id = "nonExistingId"
        every { userRepository.existsById(id) } returns false

        val result = userService.isIdExists(id)

        assertFalse(result)
    }
}