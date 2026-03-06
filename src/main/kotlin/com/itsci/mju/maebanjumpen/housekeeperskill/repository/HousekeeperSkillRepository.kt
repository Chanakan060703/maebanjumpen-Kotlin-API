package com.itsci.mju.maebanjumpen.housekeeperskill.repository

import com.itsci.mju.maebanjumpen.entity.HousekeeperSkill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HousekeeperSkillRepository : JpaRepository<HousekeeperSkill, Long> {

    @Query("SELECT hs FROM HousekeeperSkill hs WHERE hs.housekeeper.id = :housekeeperId AND hs.skillType.id = :skillTypeId")
    fun findByHousekeeperIdAndSkillTypeId(
        @Param("housekeeperId") housekeeperId: Long,
        @Param("skillTypeId") skillTypeId: Long
    ): Optional<HousekeeperSkill>

    fun findByHousekeeperId(housekeeperId: Long): List<HousekeeperSkill>
}

