package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "hire")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Hire(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0,

    @Column(name = "name", nullable = false)
    var hireName: String = "",

    @Column(name = "detail", nullable = false)
    var hireDetail: String = "",

    @Column(nullable = false)
    var paymentAmount: Double = 0.0,

    @Column(nullable = false)
    var hireDate: LocalDateTime? = null,

    @Column(nullable = false)
    var startDate: LocalDate? = null,

    @Column(nullable = false)
    var startTime: LocalTime? = null,

    @Column(nullable = true)
    var endTime: LocalTime? = null,

    @Column(nullable = false)
    var location: String = "",

    @Column(nullable = false)
    var jobStatus: String = "",

    @Column(name = "hirer_id", insertable = false, updatable = false)
    var hirerId: Long? = null,

    @Column(name = "housekeeper_id", insertable = false, updatable = false)
    var housekeeperId: Long? = null,

    @Column(name = "skill_type_id", insertable = false, updatable = false)
    var skillTypeId: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hirer_id")
    var hirer: Hirer? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "housekeeper_id")
    var housekeeper: Housekeeper? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_type_id")
    var skillType: SkillType? = null,

    @OneToOne(mappedBy = "hire", fetch = FetchType.LAZY)
    var review: Review? = null,

    @Column(name = "create_at")
    var createAt: java.time.LocalDateTime? = null,

    @Column(name = "update_at")
    var updateAt: java.time.LocalDateTime? = null,

    @Column(name = "is_delete")
    var isDelete: Boolean? = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hire) return false
        return id != null && id == other.id


    }


    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Hire(hireId=$id, hireName='$hireName', jobStatus='$jobStatus')"

    fun toHireDTO(): HireDTO {
        return HireDTO(
            id = id,
            hireName = hireName,
            hireDetail = hireDetail,
            paymentAmount = paymentAmount,
            hireDate = hireDate,
            startDate = startDate,
            startTime = startTime,
            endTime = endTime,
            location = location,
            jobStatus = jobStatus
        )
    }
}