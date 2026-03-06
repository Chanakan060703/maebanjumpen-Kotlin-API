package com.itsci.mju.maebanjumpen.hire.repository

import com.itsci.mju.maebanjumpen.entity.Hire
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HireRepository : JpaRepository<Hire, Long> {

    @EntityGraph(attributePaths = ["hirer", "housekeeper", "skillType"])
    @Query("SELECT h FROM Hire h")
    fun findAllWithDetails(): List<Hire>

    @EntityGraph(attributePaths = ["hirer", "hirer.person", "housekeeper", "housekeeper.person", "skillType"])
    @Query("SELECT h FROM Hire h WHERE h.id = :id")
    fun findByIdWithAllDetails(@Param("id") id: Long): Optional<Hire>

    @EntityGraph(attributePaths = ["hirer", "housekeeper", "skillType"])
    @Query("SELECT h FROM Hire h WHERE h.hirer.id = :hirerId")
    fun findByHirerIdWithDetails(@Param("hirerId") hirerId: Long): List<Hire>

    @EntityGraph(attributePaths = ["hirer", "housekeeper", "skillType"])
    @Query("SELECT h FROM Hire h WHERE h.housekeeper.id = :housekeeperId")
    fun findByHousekeeperIdWithDetails(@Param("housekeeperId") housekeeperId: Long): List<Hire>

    @EntityGraph(attributePaths = ["hirer", "housekeeper", "skillType"])
    @Query("SELECT h FROM Hire h WHERE h.housekeeper.id = :housekeeperId AND h.jobStatus = :jobStatus")
    fun findByHousekeeperIdAndJobStatusWithDetails(
        @Param("housekeeperId") housekeeperId: Long,
        @Param("jobStatus") jobStatus: String
    ): List<Hire>
}

