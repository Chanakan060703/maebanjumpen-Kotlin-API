package com.itsci.mju.maebanjumpen.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "skill_level_tier")
data class SkillLevelTier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name")
    var skillLevelName: String? = null,

    @Column(name = "min_hires_for_level")
    var minHiresForLevel: Int? = null,

    @Column(name = "create_at")
    var createAt: LocalDateTime? = null,

    @Column(name = "update_at")
    var updateAt: LocalDateTime? = null,

    @Column(name = "is_delete")
    var isDelete: Boolean? = false
)

