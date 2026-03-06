package com.itsci.mju.maebanjumpen.penalty.service

import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO

interface PenaltyService {
    fun getAllPenalties(): List<PenaltyDTO>
    fun getPenaltyById(id: Long): PenaltyDTO?

    @Deprecated("Use savePenalty(PenaltyDTO, Long) instead")
    fun savePenalty(penaltyDto: PenaltyDTO): PenaltyDTO

    fun savePenalty(penaltyDto: PenaltyDTO, targetRoleId: Long): PenaltyDTO
    fun deletePenalty(id: Long)
    fun updatePenalty(id: Long, penaltyDto: PenaltyDTO): PenaltyDTO
}

