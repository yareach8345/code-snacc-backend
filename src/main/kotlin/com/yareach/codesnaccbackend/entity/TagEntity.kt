package com.yareach.codesnaccbackend.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tags")
class TagEntity (
    @Id
    val tag: String
)