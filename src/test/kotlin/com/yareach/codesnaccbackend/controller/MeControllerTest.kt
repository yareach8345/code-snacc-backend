package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.user.UpdateField
import com.yareach.codesnaccbackend.dto.user.UserInfoUpdateDto
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.text.onEach

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = ["classpath:db/scripts/init-users.sql"])
@Transactional
class MeControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    @WithMockUser(username = "test-user1", roles = ["USER"])
    fun updateUserInfo() {
        val updateDto = UserInfoUpdateDto(
            password = UpdateField(value = "MyAwesomeAndHardNewPassword"),
            nickname = UpdateField(value = "AyAwesomeNewName")
        )

        mockMvc
            .perform(
                patch("/me")
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
                patch("/me")
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
            .perform(patch("/me/quit"))
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
            .perform(delete("/me/quit"))
            .andExpect(status().isUnauthorized)
            .andReturn()
            .response
            .contentAsString
            .let(::println)
    }

    @Test
    @WithMockUser(username = "test-user1", password = "<PASSWORD>")
    fun getMyInfo() {
        mockMvc
            .perform(get("/me"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/users/test-user1"))
            .andReturn()
            .response
            .contentAsString
            .onEach { println(it) }
    }

    @Test
    fun getMyInfoWithLogin() {
        mockMvc
            .perform(get("/me"))
            .andExpect(status().isUnauthorized)
            .andReturn()
            .response
            .contentAsString
            .let { println(it) }
    }
}