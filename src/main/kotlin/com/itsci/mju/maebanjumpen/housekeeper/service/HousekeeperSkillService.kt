package com.itsci.mju.maebanjumpen.housekeeperskill.service

import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import java.util.Optional

interface HousekeeperSkillService {
    fun getAllHousekeeperSkills(): List<HousekeeperDTO>
    fun getHousekeeperSkillById(id: Long): HousekeeperSkillDTO?
    fun saveHousekeeperSkill(housekeeperSkillDto: HousekeeperSkillDTO): HousekeeperSkillDTO
    fun deleteHousekeeperSkill(id: Long)
    fun getSkillsByHousekeeperId(housekeeperId: Long): Optional<HousekeeperSkillDTO>
    fun updateHousekeeperSkill(id: Long, skillDto: HousekeeperSkillDTO): HousekeeperSkillDTO
    fun updateSkillLevelAndHiresCompleted(housekeeperId: Long, skillTypeId: Int)
    fun findByHousekeeperIdAndSkillTypeId(housekeeperId: Long, skillTypeId: Int): Optional<HousekeeperSkillDTO>
}

