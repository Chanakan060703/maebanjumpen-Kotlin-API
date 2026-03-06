package com.itsci.mju.maebanjumpen.partyrole.repository

import com.itsci.mju.maebanjumpen.entity.Housekeeper
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HousekeeperRepository : JpaRepository<Housekeeper, Long> {

    @EntityGraph(attributePaths = ["person", "housekeeperSkills"])
    @Query("SELECT h FROM Housekeeper h")
    fun findAllWithPersonAndSkills(): List<Housekeeper>

    @EntityGraph(attributePaths = ["person", "housekeeperSkills", "housekeeperSkills.skillType", "housekeeperSkills.skillLevelTier"])
    @Query("SELECT h FROM Housekeeper h WHERE h.id = :id")
    fun findByIdWithAllDetails(@Param("id") id: Long): Optional<Housekeeper>

    @Query("SELECT AVG(h.rating) FROM Housekeeper h WHERE h.id = :housekeeperId")
    fun calculateAverageRatingByHousekeeperId(@Param("housekeeperId") housekeeperId: Long): Double?

    @EntityGraph(attributePaths = ["person", "housekeeperSkills"])
    @Query("SELECT h FROM Housekeeper h WHERE h.statusVerify = :statusVerify")
    fun findByStatusVerifyWithDetails(@Param("statusVerify") statusVerify: String): List<Housekeeper>

    @EntityGraph(attributePaths = ["person", "housekeeperSkills"])
    @Query("SELECT h FROM Housekeeper h WHERE h.statusVerify IS NULL OR h.statusVerify != 'VERIFIED'")
    fun findNotVerifiedOrNullStatusHousekeepersWithDetails(): List<Housekeeper>
}

