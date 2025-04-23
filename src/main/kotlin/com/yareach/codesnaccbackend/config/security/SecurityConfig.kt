package com.yareach.codesnaccbackend.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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
                .requestMatchers("/logout").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/user/quit").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/user/{userId}").authenticated()
                .anyRequest().permitAll()}
            .formLogin { it
                .loginProcessingUrl("/user/login")
                .successHandler (CustomAuthenticationSuccessHandler())
                .failureHandler (CustomAuthenticationFailureHandler())
                .permitAll() }
            .logout { it
                .logoutUrl("/logout")
                .logoutSuccessHandler(CustomLogoutSuccessHandler()) }
            .csrf{ it.disable() }
            .build()
}