package com.yareach.codesnaccbackend.config.security

import com.yareach.codesnaccbackend.entity.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(private val userEntity: UserEntity): UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return listOf(SimpleGrantedAuthority(userEntity.role.name))
    }

    override fun getPassword(): String? {
        return userEntity.password
    }

    override fun getUsername(): String? {
        return userEntity.id
    }

    override fun isAccountNonLocked(): Boolean = !userEntity.banned
    override fun isEnabled(): Boolean = !userEntity.quit && !userEntity.banned
}