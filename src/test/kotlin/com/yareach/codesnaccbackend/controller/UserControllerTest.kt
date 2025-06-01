package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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
    lateinit var objectMapper: ObjectMapper

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
                post("/users" )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinDto)) )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/users/${joinDto.id}"))
            .andReturn()
            .response

        //데이터베이스에 유저 정보가 저장되고
        val newUserEntity = userRepository.findByIdOrNull(joinDto.id)
        assertNotNull(newUserEntity)

        //추가된 유저의 아이디로 유저의 정보를 불러올 수 있다
        mockMvc
            .perform(get("/users/${joinDto.id}"))
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
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinDto)))
            .andExpect(status().is4xxClientError)
            .andExpect(jsonPath("$.code").value("USER_ID_DUPLICATE"))
            .andReturn()
    }

    @Test
    fun getUserInfo() {
        mockMvc
            .perform(get("/users/test-user1"))
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
            .perform(get("/users/test-user1/check-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isExists").value(true))
            .andReturn()
            .response
            .contentAsString
            .let(::println)

        mockMvc
            .perform(get("/users/unExistsUser/check-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isExists").value(false))
            .andReturn()
            .response
            .contentAsString
            .let(::println)
    }
}