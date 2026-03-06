package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperDetailDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO

interface HousekeeperService {
    fun getAllHousekeepers(): List<HousekeeperDTO>
    fun getHousekeeperDetailById(id: Long): HousekeeperDetailDTO?
    fun saveHousekeeper(housekeeperDto: HousekeeperDTO): HousekeeperDTO
    fun updateHousekeeper(id: Long, housekeeperDto: HousekeeperDTO): HousekeeperDTO
    fun deleteHousekeeper(id: Long)
    fun calculateAndSetAverageRating(housekeeperId: Long)
    fun addBalance(housekeeperId: Long, amount: Double)
    fun deductBalance(housekeeperId: Long, amount: Double)
    fun getHousekeepersByStatus(status: String): List<HousekeeperDTO>
    fun getNotVerifiedOrNullStatusHousekeepers(): List<HousekeeperDTO>
}

