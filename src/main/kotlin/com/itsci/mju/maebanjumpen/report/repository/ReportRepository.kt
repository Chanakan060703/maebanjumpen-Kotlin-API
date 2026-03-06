package com.itsci.mju.maebanjumpen.report.repository

import com.itsci.mju.maebanjumpen.entity.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ReportRepository : JpaRepository<Report, Long> {

    fun findByReportStatus(reportStatus: String): List<Report>

    @Query("SELECT r FROM Report r JOIN r.penalties p WHERE p.id = :penaltyId")
    fun findByPenaltyId(@Param("penaltyId") penaltyId: Long): Optional<Report>

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.penalties WHERE r.reporter.person.id = :personId")
    fun findReportsWithPenaltyByPersonId(@Param("personId") personId: Long): List<Report>

    @Query("SELECT r FROM Report r WHERE r.hire.id = :hireId")
    fun findByHireId(@Param("hireId") hireId: Long): List<Report>

    @Query("SELECT r FROM Report r WHERE r.hire.id = :hireId AND r.reporter.id = :reporterId")
    fun findByHireIdAndReporterId(@Param("hireId") hireId: Long, @Param("reporterId") reporterId: Long): Optional<Report>
}

