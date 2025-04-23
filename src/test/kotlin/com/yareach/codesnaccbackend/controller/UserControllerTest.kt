package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.user.UserInfoUpdateDto
import com.yareach.codesnaccbackend.dto.user.UserJoinDto
import com.yareach.codesnaccbackend.entity.UserEntity
import com.yareach.codesnaccbackend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    val objectMapper = ObjectMapper()

    @BeforeEach
    fun setDefaultUsers() {
        val testUser1 = UserEntity(
            id = "testId1",
            password = "<PASSWORD>",
            nickName = "testNickName1"
        )

        val testUser2 = UserEntity(
            id = "testId2",
            password = "<PASSWORD>",
        )

        userRepository.save(testUser1)
        userRepository.save(testUser2)
    }

    @Test
    fun join() {
        val joinDto = UserJoinDto(
            id = "newUser",
            password = "<PASSWORD>",
            nickName = "testing"
        )

        //유저 정보가 등록되면
        mockMvc
            .perform(
                post("/user" )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinDto)) )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/user/${joinDto.id}"))
            .andReturn()
            .response

        //데이터베이스에 유저 정보가 저장되고
        val newUserEntity = userRepository.findByIdOrNull(joinDto.id)
        assertNotNull(newUserEntity)

        //추가된 유저의 아이디로 유저의 정보를 불러올 수 있다
        mockMvc
            .perform(get("/user/${joinDto.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(joinDto.id))
            .andExpect(jsonPath("$.nickName").value(joinDto.nickName))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.banned").value(false))
            .andExpect(jsonPath("$.quit").value(false))
            .andExpect(jsonPath("$.warnCnt").value(0))
    }

    @Test
    fun joinDuplicateId() {
        val joinDto = UserJoinDto(
            id = "testId1",
            password = "<PASSWORD>",
            nickName = "testing"
        )

        mockMvc
            .perform (
                post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinDto)))
            .andExpect(status().is4xxClientError)
            .andExpect(jsonPath("$.code").value("USER_ID_DUPLICATE"))
            .andReturn()
    }

    @Test
    @WithMockUser(username = "testId1", password = "<PASSWORD>")
    fun getMyInfo() {
        mockMvc
            .perform(get("/user/me"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/user/testId1"))
            .andReturn()
            .response
            .contentAsString
            .onEach { println(it) }
    }

    @Test
    fun getMyInfoWithLogin() {
        mockMvc
            .perform(get("/user/me"))
            .andExpect(status().isUnauthorized)
            .andReturn()
            .response
            .contentAsString
            .let { println(it) }
    }

    @Test
    @WithMockUser(username = "testId1", roles = ["USER"])
    fun updateUserInfo() {
        val updateDto = UserInfoUpdateDto(
            password = "MyAwesomeAndHardNewPassword",
            nickname = "AyAwesomeNewName"
        )

        mockMvc
            .perform(
                patch("/user/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("testId1"))
            .andExpect(jsonPath("$.nickName").value("AyAwesomeNewName"))
            .andReturn()
            .response
            .contentAsString
            .let{ println(it) }

        val updatedUser = userRepository.findByIdOrNull("testId1")
        assertNotNull(updatedUser)
        assertEquals("AyAwesomeNewName", updatedUser?.nickName)
        assertTrue(bCryptPasswordEncoder.matches(updateDto.password, updatedUser?.password))
    }

    @Test
    fun updateUserInfoWithoutLogin() {
        val updateDto = UserInfoUpdateDto(
            password = "MyAwesomeAndHardNewPassword",
            nickname = "AyAwesomeNewName"
        )

        mockMvc
            .perform(
                patch("/user/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isUnauthorized)
            .andReturn()
            .response
            .contentAsString
            .let{ println(it) }
    }

    @Test
    @WithMockUser(username = "testId1", roles = ["USER"])
    fun quit() {
        mockMvc
            .perform(delete("/user/quit"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
            .let(::println)

        val quitUser = userRepository.findByIdOrNull("testId1")
        assertNotNull(quitUser)
        assertTrue(quitUser?.quit!!)
        assertFalse(quitUser.banned)
    }

    @Test
    fun quitWithoutLogin() {
        mockMvc
            .perform(delete("/user/quit"))
            .andExpect(status().isUnauthorized)
            .andReturn()
            .response
            .contentAsString
            .let(::println)
    }

    @Test
    fun getUserInfo() {
        mockMvc
            .perform(get("/user/testId1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("testId1"))
            .andExpect(jsonPath("$.nickName").value("testNickName1"))
            .andReturn()
            .response
            .contentAsString
    }

    @Test
    fun isIdExists() {
        mockMvc
            .perform(get("/user/testId1/check-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isExists").value(true))
            .andReturn()
            .response
            .contentAsString
            .let(::println)

        mockMvc
            .perform(get("/user/unExistsUser/check-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isExists").value(false))
            .andReturn()
            .response
            .contentAsString
            .let(::println)
    }
}