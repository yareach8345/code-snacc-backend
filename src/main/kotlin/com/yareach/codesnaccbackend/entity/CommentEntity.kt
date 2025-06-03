package com.yareach.codesnaccbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "comments")
class CommentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    var id: Int? = null,

    var content: String,

    @ManyToOne
    @JoinColumn(name = "post_id")
    var post: PostEntity,

    @ManyToOne
    @JoinColumn(name = "writer")
    var writer: UserEntity,

    @Column(name = "written_at")
    var writtenAt: LocalDateTime? = null,

    var deleted: Boolean = false,
)