package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.partyrole.dto.HirerDTO

interface HirerService {
    fun saveHirer(hirerDto: HirerDTO): HirerDTO
    fun getHirerById(id: Long): HirerDTO
    fun getAllHirers(): List<HirerDTO>
    fun updateHirer(id: Long, hirerDto: HirerDTO): HirerDTO
    fun deleteHirer(id: Long)
    fun deductBalance(hirerId: Long, amount: Double)
    fun addBalance(hirerId: Long, amount: Double)
}

