package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "penalty")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Penalty(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    @Column(name = "penalty_type", nullable = false)
    var penaltyType: String = "",

    @Column(name = "penalty_detail", nullable = false)
    var penaltyDetail: String = "",

    @Column(name = "penalty_date", nullable = false)
    var penaltyDate: LocalDateTime? = null,

    @Column(name = "penalty_status", nullable = false)
    var penaltyStatus: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    var report: Report? = null,

    @Column(name = "create_at")
    var createAt: LocalDateTime? = null,

    @Column(name = "update_at")
    var updateAt: LocalDateTime? = null,

    @Column(name = "is_delete")
    var isDelete: Boolean? = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Penalty) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Penalty(penaltyId=$id, penaltyType='$penaltyType', penaltyStatus='$penaltyStatus')"
}

