package com.itsci.mju.maebanjumpen.housekeeperskill.service.impl

import com.itsci.mju.maebanjumpen.entity.HousekeeperSkill
import com.itsci.mju.maebanjumpen.entity.SkillLevelTier
import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO
import com.itsci.mju.maebanjumpen.housekeeperskill.repository.HousekeeperSkillRepository
import com.itsci.mju.maebanjumpen.housekeeperskill.service.HousekeeperSkillService
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.HousekeeperRepository
import com.itsci.mju.maebanjumpen.skilltype.repository.SkillLevelTierRepository
import com.itsci.mju.maebanjumpen.skilltype.repository.SkillTypeRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
@Transactional
class HousekeeperSkillServiceImpl(
    private val housekeeperSkillRepository: HousekeeperSkillRepository,
    private val skillLevelTierRepository: SkillLevelTierRepository,
    private val housekeeperRepository: HousekeeperRepository,
    private val skillTypeRepository: SkillTypeRepository
) : HousekeeperSkillService {

    private fun mapHousekeeperSkillToDto(hs: HousekeeperSkill): HousekeeperSkillDTO {
        return HousekeeperSkillDTO(
            id = hs.id,
            housekeeperId = hs.housekeeper?.id?.toInt(),
            skillTypeId = hs.skillType?.id?.toInt(),
            skillLevelTierId = hs.skillLevelTier?.id?.toInt(),
            pricePerDay = hs.pricePerDay,
            totalHiresCompleted = hs.totalHiresCompleted
        )
    }

    @Transactional(readOnly = true)
    override fun getAllHousekeeperSkills(): List<HousekeeperDTO> {
        val housekeepers = housekeeperRepository.findAll()
        return housekeepers.map { hk ->
            HousekeeperDTO().apply {
                id = hk.id
                balance = hk.balance
                photoVerifyUrl = hk.photoVerifyUrl
                statusVerify = hk.statusVerify
                rating = hk.rating
                dailyRate = hk.dailyRate
            }
        }
    }

    @Transactional(readOnly = true)
    override fun getHousekeeperSkillById(id: Long): HousekeeperSkillDTO? {
        val entity = housekeeperSkillRepository.findById(id).orElse(null)
        return entity?.let { mapHousekeeperSkillToDto(it) }
    }

    override fun saveHousekeeperSkill(housekeeperSkillDto: HousekeeperSkillDTO): HousekeeperSkillDTO {
        val housekeeperId = housekeeperSkillDto.housekeeperId?.toLong()
            ?: throw IllegalArgumentException("Housekeeper ID is required")
        val skillTypeId = housekeeperSkillDto.skillTypeId
            ?: throw IllegalArgumentException("SkillType ID is required")

        val optionalExistingHs = housekeeperSkillRepository
            .findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId, skillTypeId)

        if (optionalExistingHs.isPresent) {
            val existingSkill = optionalExistingHs.get()
            housekeeperSkillDto.pricePerDay?.let { existingSkill.pricePerDay = it }
            val updatedSkill = housekeeperSkillRepository.save(existingSkill)
            return mapHousekeeperSkillToDto(updatedSkill)
        }

        val housekeeper = housekeeperRepository.findById(housekeeperId)
            .orElseThrow { EntityNotFoundException("Housekeeper not found with ID: $housekeeperId") }

        val skillType = skillTypeRepository.findById(skillTypeId.toLong())
            .orElseThrow { EntityNotFoundException("SkillType not found with ID: $skillTypeId") }

        val skillLevelTier = if (housekeeperSkillDto.skillLevelTierId != null) {
            skillLevelTierRepository.findById(housekeeperSkillDto.skillLevelTierId!!.toLong())
                .orElseThrow { EntityNotFoundException("SkillLevelTier not found with ID: ${housekeeperSkillDto.skillLevelTierId}") }
        } else {
            skillLevelTierRepository.findAll()
                .minByOrNull { it.minHiresForLevel ?: 0 }
                ?: throw RuntimeException("No SkillLevelTier found. Cannot set initial level.")
        }

        val entity = HousekeeperSkill(
            housekeeper = housekeeper,
            skillType = skillType,
            skillLevelTier = skillLevelTier,
            pricePerDay = housekeeperSkillDto.pricePerDay,
            totalHiresCompleted = 0
        )

        val savedEntity = housekeeperSkillRepository.save(entity)
        return mapHousekeeperSkillToDto(savedEntity)
    }

    override fun deleteHousekeeperSkill(id: Long) {
        housekeeperSkillRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun getSkillsByHousekeeperId(housekeeperId: Long): Optional<HousekeeperSkillDTO> {
        val optionalHousekeeper = housekeeperRepository.findById(housekeeperId)

        if (optionalHousekeeper.isPresent && optionalHousekeeper.get().housekeeperSkills?.isNotEmpty() == true) {
            val firstSkill = optionalHousekeeper.get().housekeeperSkills!!.iterator().next()
            return Optional.of(mapHousekeeperSkillToDto(firstSkill))
        }

        return Optional.empty()
    }

    override fun updateHousekeeperSkill(id: Long, skillDto: HousekeeperSkillDTO): HousekeeperSkillDTO {
        return housekeeperSkillRepository.findById(id).map { existingSkill ->
            skillDto.pricePerDay?.let { existingSkill.pricePerDay = it }
            skillDto.totalHiresCompleted?.let { existingSkill.totalHiresCompleted = it }

            skillDto.skillLevelTierId?.let { tierId ->
                val newTier = skillLevelTierRepository.findById(tierId.toLong())
                    .orElseThrow { EntityNotFoundException("SkillLevelTier not found with ID: $tierId") }
                existingSkill.skillLevelTier = newTier
            }

            val updatedSkill = housekeeperSkillRepository.save(existingSkill)
            mapHousekeeperSkillToDto(updatedSkill)
        }.orElseThrow { NoSuchElementException("HousekeeperSkill not found with ID: $id") }
    }

    @Transactional(readOnly = true)
    override fun findByHousekeeperIdAndSkillTypeId(housekeeperId: Long, skillTypeId: Int): Optional<HousekeeperSkillDTO> {
        val optionalHs = housekeeperSkillRepository.findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId, skillTypeId)
        return optionalHs.map { mapHousekeeperSkillToDto(it) }
    }

    override fun updateSkillLevelAndHiresCompleted(housekeeperId: Long, skillTypeId: Int) {
        val optionalHs = housekeeperSkillRepository.findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId, skillTypeId)

        if (optionalHs.isPresent) {
            val hs = optionalHs.get()
            hs.totalHiresCompleted = (hs.totalHiresCompleted ?: 0) + 1
            recalculateSkillLevel(hs)
            housekeeperSkillRepository.save(hs)
        } else {
            System.err.println("HousekeeperSkill not found for housekeeper $housekeeperId and skill $skillTypeId")
        }
    }

    private fun recalculateSkillLevel(hs: HousekeeperSkill) {
        val tiers = skillLevelTierRepository.findAll()
            .sortedByDescending { it.minHiresForLevel ?: 0 }

        val currentHires = hs.totalHiresCompleted ?: 0
        var newTier: SkillLevelTier? = hs.skillLevelTier

        for (tier in tiers) {
            if (currentHires >= (tier.minHiresForLevel ?: 0)) {
                newTier = tier
                break
            }
        }

        if (newTier != null && newTier != hs.skillLevelTier) {
            hs.skillLevelTier = newTier
        }
    }
}

