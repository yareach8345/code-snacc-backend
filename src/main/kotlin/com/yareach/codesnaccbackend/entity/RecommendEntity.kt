package com.yareach.codesnaccbackend.entity

import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Embeddable
class RecommendEntityId (
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity,

    @ManyToOne
    @JoinColumn(name = "post_id")
    var post: PostEntity
)

@Entity()
@Table(name = "recommends")
class RecommendEntity (
    @EmbeddedId
    var recommendId: RecommendEntityId,
)