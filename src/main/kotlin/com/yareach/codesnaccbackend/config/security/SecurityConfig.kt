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
                .requestMatchers("/user/me").authenticated()
                .anyRequest().permitAll()}
            .formLogin { it
                .loginProcessingUrl("/auth/login")
                .successHandler (CustomAuthenticationSuccessHandler())
                .failureHandler (CustomAuthenticationFailureHandler())
                .permitAll() }
            .logout { it
                .logoutUrl("/auth/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(CustomLogoutSuccessHandler()) }
            .csrf { it.disable() }
            .cors {}
            .exceptionHandling { it
                .authenticationEntryPoint(CustomAuthenticationEntryPoint())
            }
            .build()
}