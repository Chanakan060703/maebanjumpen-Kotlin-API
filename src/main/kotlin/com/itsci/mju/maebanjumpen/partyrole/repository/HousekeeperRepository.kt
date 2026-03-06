package com.itsci.mju.maebanjumpen.partyrole.repository

import com.itsci.mju.maebanjumpen.entity.Housekeeper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HousekeeperRepository : JpaRepository<Housekeeper, Long> {

    fun findAllWithPersonLoginAndSkills(): List<Housekeeper>

    fun findByIdWithAllDetails(id: Long): Optional<Housekeeper>

    fun calculateAverageRatingByHousekeeperId(housekeeperId: Long): Double?

    fun findByStatusVerifyWithDetails(statusVerify: String): List<Housekeeper>

    fun findNotVerifiedOrNullStatusHousekeepersWithDetails(): List<Housekeeper>
}

