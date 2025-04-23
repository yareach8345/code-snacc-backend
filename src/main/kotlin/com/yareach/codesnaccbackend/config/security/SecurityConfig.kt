package com.yareach.codesnaccbackend.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests { it
                .requestMatchers("/user/**", "/user").permitAll()
                .anyRequest().permitAll()}
            .formLogin { it
                .loginProcessingUrl("/user/login")
                .successHandler (CustomAuthenticationSuccessHandler())
                .permitAll() }
            .logout { it
                .logoutUrl("/user/logout") }
            .csrf{ it.disable() }
            .build()
}