package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "housekeeper_skill")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class HousekeeperSkill(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_level_tier_id", nullable = false)
    var skillLevelTier: SkillLevelTier? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housekeeper_id", nullable = false)
    @JsonBackReference("housekeeper-skills")
    var housekeeper: Housekeeper? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_type_id", nullable = false)
    var skillType: SkillType? = null,

    @Column(name = "price_per_day")
    var pricePerDay: Double? = null,

    @Column(name = "total_hires_completed", nullable = false)
    var totalHiresCompleted: Int = 0,

    @Column(name = "create_at")
    var createAt: LocalDateTime? = null,

    @Column(name = "update_at")
    var updateAt: LocalDateTime? = null,

    @Column(name = "is_delete")
    var isDelete: Boolean? = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HousekeeperSkill) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "HousekeeperSkill(skillId=$id, pricePerDay=$pricePerDay)"
}

