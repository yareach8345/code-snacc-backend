package com.yareach.codesnaccbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault

enum class UserRole {
    ADMIN, USER
}

@Entity
@Table(name = "users")
class UserEntity (
    @Id
    @Column(name = "user_id")
    val id: String,

    @Column(nullable = false)
    var password: String,

    @Column(name = "nickname", nullable = true)
    var nickname: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER,

    @Column(nullable = false)
    var banned: Boolean = false,

    @Column(nullable = false)
    var quit: Boolean = false,

    @Column(name = "warn_cnt", nullable = false)
    var warnCnt: Byte = 0,

    @Column(name = "user_icon")
    @ColumnDefault("'mdi-account-circle'")
    var icon: String? = "mdi-account-circle"
) {
    fun quit() {
        quit = true
    }

    @PrePersist
    fun iconNullCheck() {
        if (icon == null) icon = "mdi-account-circle"
    }
}