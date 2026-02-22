package com.yareach.codesnaccbackend.config.security

import com.yareach.codesnaccbackend.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val userDetailsService: CustomUserDetailsService
) {

    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests { it
                .requestMatchers("/logout").authenticated()
                .requestMatchers("/users/me/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/posts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE , "/posts/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/posts/**").authenticated()
                .anyRequest().permitAll()}
            .formLogin { it
                .loginProcessingUrl("/auth/login")
                .successHandler (CustomAuthenticationSuccessHandler())
                .failureHandler (CustomAuthenticationFailureHandler())
                .permitAll() }
            .rememberMe { it
                .rememberMeParameter("rememberMe")
                .tokenValiditySeconds(60 * 60 * 24 * 7)
                .userDetailsService(userDetailsService)
            }
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