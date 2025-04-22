package com.yareach.codesnaccbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

enum class UserRole {
    ADMIN, USER
}

@Entity
@Table(name = "users")
class UserEntity (
    @Id
    @Column(name = "user_id")
    val id: String,

    var password: String,

    @Column(name = "nickname")
    var nickName: String?,

    @Enumerated(EnumType.STRING)
    var role: UserRole,

    var banned: Boolean,

    var quit: Boolean,

    @Column(name = "warn_cnt")
    var warnCnt: Byte
)