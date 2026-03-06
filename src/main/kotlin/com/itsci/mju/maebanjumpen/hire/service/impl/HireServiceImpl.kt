package com.itsci.mju.maebanjumpen.hire.service.impl


import com.itsci.mju.maebanjumpen.entity.Hire
import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.hire.repository.HireRepository
import com.itsci.mju.maebanjumpen.hire.request.CreateHireRequest
import com.itsci.mju.maebanjumpen.hire.request.UpdateHireRequest
import com.itsci.mju.maebanjumpen.hire.service.HireService
import com.itsci.mju.maebanjumpen.housekeeperskill.service.HousekeeperSkillService
import com.itsci.mju.maebanjumpen.partyrole.repository.HirerRepository
import com.itsci.mju.maebanjumpen.partyrole.repository.HousekeeperRepository
import com.itsci.mju.maebanjumpen.partyrole.service.HirerService
import com.itsci.mju.maebanjumpen.partyrole.service.HousekeeperService
import com.itsci.mju.maebanjumpen.skilltype.repository.SkillTypeRepository
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
class HireServiceImpl(
    private val hireRepository: HireRepository,
    private val hirerService: HirerService,
    private val housekeeperService: HousekeeperService,
    private val housekeeperSkillService: HousekeeperSkillService,
    private val skillTypeRepository: SkillTypeRepository,
    private val hirerRepository: HirerRepository,
    private val housekeeperRepository: HousekeeperRepository,
    private val statusUpdateService: HireStatusUpdateService
) : HireService {

  private fun mapHireToDto(hire: Hire): HireDTO {
    return HireDTO(
        id = hire.id,
        hireName = hire.hireName,
        hireDetail = hire.hireDetail,
        paymentAmount = hire.paymentAmount,
        hireDate = hire.hireDate,
        startDate = hire.startDate,
        startTime = hire.startTime,
        endTime = hire.endTime,
        location = hire.location,
        jobStatus = hire.jobStatus,
        skillType = hire.skillType?.let { it.toSkillTypeDTO(it.id) }
    )
  }

  override fun listAllHires(): List<HireDTO> {
    return hireRepository.findAllWithDetails().map { mapHireToDto(it) }
  }

  override fun getHireById(id: Long): HireDTO {
    val hire = hireRepository.findByIdWithAllDetails(id)
        .orElseThrow { RuntimeException("Hire not found with ID: $id") }
    return mapHireToDto(hire)
  }

  override fun getHiresByHirerId(hirerId: Long): List<HireDTO> {
    hirerRepository.findById(hirerId)
        .orElseThrow { RuntimeException("Hirer not found with ID: $hirerId") }
    return hireRepository.findByHirerIdWithDetails(hirerId).map { mapHireToDto(it) }
  }

  override fun getHiresByHousekeeperId(housekeeperId: Long): List<HireDTO> {
    housekeeperRepository.findById(housekeeperId)
        .orElseThrow { RuntimeException("Housekeeper not found with ID: $housekeeperId") }
    return hireRepository.findByHousekeeperIdWithDetails(housekeeperId).map { mapHireToDto(it) }
  }

  @Transactional
  override fun createHire(request: CreateHireRequest): HireDTO {
    val hirerId = request.hirerId ?: throw RuntimeException("Hirer ID is required")
    val housekeeperId = request.housekeeperId ?: throw RuntimeException("Housekeeper ID is required")
    val skillTypeId = request.skillTypeId ?: throw RuntimeException("SkillType ID is required")

    val hirer = hirerRepository.findById(hirerId)
        .orElseThrow { RuntimeException("Hirer not found with ID: $hirerId") }
    val housekeeper = housekeeperRepository.findById(housekeeperId)
        .orElseThrow { RuntimeException("Housekeeper not found with ID: $housekeeperId") }
    val skillType = skillTypeRepository.findById(skillTypeId)
        .orElseThrow { RuntimeException("SkillType not found with ID: $skillTypeId") }

    val hire = Hire(
        hireName = request.hireName,
        hireDetail = request.hireDetail,
        paymentAmount = request.paymentAmount,
        hireDate = request.hireDate,
        startDate = request.startDate,
        startTime = request.startTime,
        endTime = request.endTime,
        location = request.location,
        jobStatus = "Pending",
        hirer = hirer,
        housekeeper = housekeeper,
        skillType = skillType
    )
    val savedHire = hireRepository.save(hire)
    return mapHireToDto(savedHire)
  }

  @Transactional
  override fun updateHire(request: UpdateHireRequest): HireDTO {
    val hire = hireRepository.findById(request.id)
        .orElseThrow { RuntimeException("Hire not found with ID: ${request.id}") }

    hire.hireName = request.hireName.toString()
    hire.hireDetail = request.hireDetail.toString()
    hire.paymentAmount = request.paymentAmount!!
    hire.startDate = request.startDate?.let { LocalDate.parse(it) } ?: hire.startDate
    hire.startTime = request.startTime?.let { LocalTime.parse(it) } ?: hire.startTime
    hire.endTime = request.endTime?.let { LocalTime.parse(it) } ?: hire.endTime
    hire.location = request.location.toString()
    hire.jobStatus = request.jobStatus.toString()
//    hire.progressionImageUrls = request.imageUrls?.toMutableList() ?: hire.progressionImageUrls


    val updatedHire = hireRepository.save(hire)
    return mapHireToDto(updatedHire)
  }

  @Transactional
  override fun deleteHire(id: Long) {
    if (!hireRepository.existsById(id)) {
      throw RuntimeException("Hire not found with ID: $id")
    }
    hireRepository.deleteById(id)
  }

  override fun getCompletedHiresByHousekeeperId(housekeeperId: Long): List<HireDTO> {
    return hireRepository.findByHousekeeperIdAndJobStatusWithDetails(housekeeperId, "Completed")
        .map { mapHireToDto(it) }
  }

  @Transactional
  override fun addProgressionImagesToHire(hireId: Long, imageUrls: List<String>): HireDTO {
    val hire = hireRepository.findById(hireId)
        .orElseThrow { RuntimeException("Hire not found with ID: $hireId") }
    return mapHireToDto(hire)
  }
}

