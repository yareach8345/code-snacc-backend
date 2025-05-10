package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.user.UpdateField
import com.yareach.codesnaccbackend.dto.user.UserInfoUpdateDto
import com.yareach.codesnaccbackend.dto.user.UserJoinDto
import com.yareach.codesnaccbackend.repository.UserRepository
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
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
@Sql(scripts = ["classpath:db/scripts/init-users.sql"])
@Transactional
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    val objectMapper = ObjectMapper()

    @Test
    fun join() {
        val joinDto = UserJoinDto(
            id = "newUser",
            password = "<PASSWORD>",
            nickname = null,
            icon = null
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
            .andExpect(jsonPath("$.nickname").value(joinDto.nickname))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.banned").value(false))
            .andExpect(jsonPath("$.quit").value(false))
            .andExpect(jsonPath("$.warnCnt").value(0))
    }

    @Test
    fun joinDuplicateId() {
        val joinDto = UserJoinDto(
            id = "test-user1",
            password = "<PASSWORD>",
            nickname = "testing",
            icon = null
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
    @WithMockUser(username = "test-user1", password = "<PASSWORD>")
    fun getMyInfo() {
        mockMvc
            .perform(get("/user/me"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/user/test-user1"))
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
    @WithMockUser(username = "test-user1", roles = ["USER"])
    fun updateUserInfo() {
        val updateDto = UserInfoUpdateDto(
            password = UpdateField(value = "MyAwesomeAndHardNewPassword"),
            nickname = UpdateField(value = "AyAwesomeNewName")
        )

        mockMvc
            .perform(
                patch("/user/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("test-user1"))
            .andExpect(jsonPath("$.nickname").value("AyAwesomeNewName"))
            .andReturn()
            .response
            .contentAsString
            .let{ println(it) }

        val updatedUser = userRepository.findByIdOrNull("test-user1")
        assertNotNull(updatedUser)
        assertEquals("AyAwesomeNewName", updatedUser?.nickname)
        updateDto.password?.let { assertTrue(bCryptPasswordEncoder.matches(it.value, updatedUser?.password)) }
    }

    @Test
    fun updateUserInfoWithoutLogin() {
        val updateDto = UserInfoUpdateDto(
            password = UpdateField(value = "MyAwesomeAndHardNewPassword"),
            nickname = UpdateField(value = "AyAwesomeNewName")
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
    @WithMockUser(username = "test-user1", roles = ["USER"])
    fun quit() {
        mockMvc
            .perform(delete("/user/quit"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
            .let(::println)

        val quitUser = userRepository.findByIdOrNull("test-user1")
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
            .perform(get("/user/test-user1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("test-user1"))
            .andExpect(jsonPath("$.nickname").value("the tester"))
            .andReturn()
            .response
            .contentAsString
    }

    @Test
    fun isIdExists() {
        mockMvc
            .perform(get("/user/test-user1/check-id"))
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