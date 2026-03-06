package com.itsci.mju.maebanjumpen.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "report")
data class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    @Column(name = "report_title", nullable = false)
    var reportTitle: String = "",

    @Column(name = "report_message")
    var reportMessage: String? = null,

    @Column(name = "report_date", nullable = false)
    var reportDate: LocalDateTime? = null,

    @Column(name = "report_status", nullable = false)
    var reportStatus: String = "",

    @Column(name = "reporter_id", insertable = false, updatable = false)
    var reporterId: Long? = null,

    @Column(name = "hire_id", insertable = false, updatable = false)
    var hireId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    var reporter: PartyRole? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hire_id", nullable = false)
    var hire: Hire? = null,

    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY)
    var penalties: MutableList<Penalty> = mutableListOf(),

    @Column(name = "create_at")
    var createAt: LocalDateTime? = null,

    @Column(name = "update_at")
    var updateAt: LocalDateTime? = null,

    @Column(name = "is_delete")
    var isDelete: Boolean? = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Report) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Report(reportId=$id, reportTitle='$reportTitle', reportStatus='$reportStatus')"
}

