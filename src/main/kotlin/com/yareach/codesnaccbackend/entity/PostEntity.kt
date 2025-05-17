package com.yareach.codesnaccbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "posts")
class PostEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    var id: Int? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var writer: UserEntity,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var code: String,

    @Column(name = "lang", nullable = false)
    var language: String,

    @Column(nullable = false)
    var content: String,

    @Column(nullable = false)
    var writtenAt: LocalDateTime? = null,

    var deleted: Boolean = false,

    @ManyToMany
    @JoinTable(
        name = "post_tags",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "tag")]
    )
    var tags: MutableSet<TagEntity>,

    @ManyToMany
    @JoinTable(
        name = "recommends",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var recommends: MutableSet<UserEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "post")
    var comments: MutableSet<CommentEntity> = mutableSetOf()
) {
    @PrePersist
    fun wittenAtNullCheck() {
        if (writtenAt == null)
            writtenAt = LocalDateTime.now()
    }

    fun getRecommendCount() = recommends.size

    fun getTagList() = tags.map { it.tag }
}