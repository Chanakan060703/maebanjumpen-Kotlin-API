package com.itsci.mju.maebanjumpen.penalty.service.impl

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.entity.Penalty
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository
import com.itsci.mju.maebanjumpen.penalty.constant.PenaltyStatusEnum
import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO
import com.itsci.mju.maebanjumpen.penalty.repository.PenaltyRepository
import com.itsci.mju.maebanjumpen.penalty.service.PenaltyService
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import com.itsci.mju.maebanjumpen.report.constant.ReportStatusEnum
import com.itsci.mju.maebanjumpen.report.repository.ReportRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PenaltyServiceImpl @Autowired internal constructor(
    private val penaltyRepository: PenaltyRepository,
    private val reportRepository: ReportRepository,
    private val personRepository: PersonRepository,
    private val partyRoleRepository: PartyRoleRepository
) : PenaltyService {

    private fun mapPenaltyToDto(penalty: Penalty): PenaltyDTO {
        return PenaltyDTO(
            id = penalty.id,
            penaltyType = penalty.penaltyType,
            penaltyDetail = penalty.penaltyDetail,
            penaltyDate = penalty.penaltyDate,
            penaltyStatus = PenaltyStatusEnum.fromValue(penalty.penaltyStatus),
            reportId = penalty.report?.id
        )
    }

    override fun getAllPenalties(): List<PenaltyDTO> {
        val penalties = penaltyRepository.findAll()
        return penalties.map { mapPenaltyToDto(it) }
    }

    override fun getPenaltyById(id: Long): PenaltyDTO? {
        return penaltyRepository.findById(id)
            .map { mapPenaltyToDto(it) }
            .orElse(null)
    }

    @Deprecated("Use savePenalty(PenaltyDTO, Long) instead")
    @Transactional
    override fun savePenalty(penaltyDto: PenaltyDTO): PenaltyDTO {
        throw UnsupportedOperationException("Method savePenalty(PenaltyDTO) is deprecated. Use savePenalty(PenaltyDTO, Long) instead.")
    }

    @Transactional
    override fun savePenalty(penaltyDto: PenaltyDTO, targetRoleId: Long): PenaltyDTO {
        val penalty = Penalty().apply {
            penaltyType = penaltyDto.penaltyType ?: ""
            penaltyDetail = penaltyDto.penaltyDetail ?: ""
            penaltyDate = penaltyDto.penaltyDate
            penaltyStatus = penaltyDto.penaltyStatus?.value ?: ""
        }
        val savedPenalty = penaltyRepository.save(penalty)

        val reportId = penaltyDto.reportId
        if (reportId != null) {
            val optionalReport = reportRepository.findById(reportId)

            if (optionalReport.isPresent) {
                val report = optionalReport.get()
                savedPenalty.report = report
                report.penalties.add(savedPenalty)
                report.reportStatus = ReportStatusEnum.RESOLVED.value
                reportRepository.save(report)

                updateAccountStatus(targetRoleId, savedPenalty.penaltyType ?: "")
            } else {
                System.err.println("Warning: Report ID $reportId not found for new Penalty. Linking skipped.")
            }
        } else {
            System.err.println("Error: reportId is missing from PenaltyDTO. Cannot link Penalty to Report or update account status.")
        }

        return mapPenaltyToDto(savedPenalty)
    }

    private fun updateAccountStatus(targetRoleId: Long, penaltyType: String) {
        val optionalPartyRole = partyRoleRepository.findById(targetRoleId)

        if (optionalPartyRole.isPresent) {
            val partyRole = optionalPartyRole.get()
            val personToUpdate = partyRole.person

            if (personToUpdate != null) {
                personToUpdate.accountStatus = penaltyType
                personRepository.save(personToUpdate)
                println("Updated person account status to: $penaltyType for person ID: ${personToUpdate.id}")
            } else {
                System.err.println("Error: Person object is missing for Role ID: $targetRoleId. Cannot update account status.")
            }
        } else {
            System.err.println("Error: Target PartyRole not found with ID: $targetRoleId. Cannot apply penalty.")
        }
    }

    @Transactional
    override fun deletePenalty(id: Long) {
        val penalty = penaltyRepository.findById(id)
            .orElseThrow { NotFoundException("Penalty not found with id: $id") }

        if (penalty.isDelete == true) {
            throw BadRequestException("โทษนี้ถูกลบไปแล้ว")
        }

        penalty.report?.id?.let { reportId ->
            reportRepository.findById(reportId).ifPresent { report ->
                report.penalties.remove(penalty)
                report.reportStatus = "RESOLVED"
                reportRepository.save(report)
            }
        }

        penalty.isDelete = true
        penaltyRepository.save(penalty)
    }

    @Transactional
    override fun updatePenalty(id: Long, penaltyDto: PenaltyDTO): PenaltyDTO {
        val existingPenalty = penaltyRepository.findById(id)
            .orElseThrow { RuntimeException("Penalty not found with id: $id") }

        val penaltyTypeChanged = penaltyDto.penaltyType != null &&
                existingPenalty.penaltyType != penaltyDto.penaltyType

        penaltyDto.penaltyType?.let { existingPenalty.penaltyType = it }
        penaltyDto.penaltyDetail?.let { existingPenalty.penaltyDetail = it }
        penaltyDto.penaltyDate?.let { existingPenalty.penaltyDate = it }
        penaltyDto.penaltyStatus?.let { existingPenalty.penaltyStatus = it.value }

        val updatedPenalty = penaltyRepository.save(existingPenalty)

        if (penaltyTypeChanged) {
            System.err.println("Warning: Skipping account status update in updatePenalty method because the target person ID cannot be reliably determined from the existing entities.")
        }

        return mapPenaltyToDto(updatedPenalty)
    }
}

