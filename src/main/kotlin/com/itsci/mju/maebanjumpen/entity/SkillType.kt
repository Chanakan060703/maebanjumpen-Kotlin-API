package com.itsci.mju.maebanjumpen.entity

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillTypeDTO
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "skill_type")
data class SkillType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    @Column(name = "name")
    var skillTypeName: String? = null,

    @Column(name = "detail")
    var skillTypeDetail: String? = null,

    @Column(name = "create_at")
    var createAt: LocalDateTime? = null,

    @Column(name = "update_at")
    var updateAt: LocalDateTime? = null,

    @Column(name = "is_delete")
    var isDelete: Boolean? = false
) {
    fun toSkillTypeDTO(id: Long?): SkillTypeDTO {
        return SkillTypeDTO(
            id = id,
            skillTypeName = skillTypeName,
            skillTypeDetail = skillTypeDetail
        )
    }
}

