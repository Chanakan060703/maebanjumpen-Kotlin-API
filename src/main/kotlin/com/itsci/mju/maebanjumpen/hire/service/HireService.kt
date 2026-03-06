package com.itsci.mju.maebanjumpen.hire.service

import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.hire.request.CreateHireRequest
import com.itsci.mju.maebanjumpen.hire.request.UpdateHireRequest

interface HireService {
    fun listAllHires(): List<HireDTO>

    fun getHireById(id: Long): HireDTO

    fun getHiresByHirerId(hirerId: Long): List<HireDTO>

    fun getHiresByHousekeeperId(housekeeperId: Long): List<HireDTO>

    fun createHire(request: CreateHireRequest): HireDTO

    fun updateHire(request: UpdateHireRequest): HireDTO

    fun deleteHire(id: Long)

    fun getCompletedHiresByHousekeeperId(housekeeperId: Long): List<HireDTO>

    fun addProgressionImagesToHire(hireId: Long, imageUrls: List<String>): HireDTO
}

