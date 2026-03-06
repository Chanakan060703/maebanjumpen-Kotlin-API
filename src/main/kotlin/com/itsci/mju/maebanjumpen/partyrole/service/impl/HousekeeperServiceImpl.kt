package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.entity.Housekeeper
import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperDetailDTO
import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.HousekeeperRepository
import com.itsci.mju.maebanjumpen.partyrole.service.HousekeeperService
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HousekeeperServiceImpl(
    private val housekeeperRepository: HousekeeperRepository,
    private val personRepository: PersonRepository
) : HousekeeperService {

    @Value("\${app.public-base-url}")
    private lateinit var publicBaseUrl: String

    private fun mapHousekeeperToDto(housekeeper: Housekeeper): HousekeeperDTO {
        return HousekeeperDTO().apply {
            id = housekeeper.id
            balance = housekeeper.balance
            photoVerifyUrl = housekeeper.photoVerifyUrl
            statusVerify = housekeeper.statusVerify
            rating = housekeeper.rating
            dailyRate = housekeeper.dailyRate
            housekeeperSkills = housekeeper.housekeeperSkills?.map { hs ->
                HousekeeperSkillDTO(
                    id = hs.id,
                    housekeeperId = hs.housekeeper?.id?.toInt(),
                    skillTypeId = hs.skillType?.id?.toInt(),
                    skillLevelTierId = hs.skillLevelTier?.id?.toInt(),
                    pricePerDay = hs.pricePerDay,
                    totalHiresCompleted = hs.totalHiresCompleted
                )
            }?.toSet()
        }
    }

    private fun mapHousekeeperToDetailDto(housekeeper: Housekeeper): HousekeeperDetailDTO {
        return HousekeeperDetailDTO(
            detailId = housekeeper.id,
            detailBalance = housekeeper.balance,
            detailPhotoVerifyUrl = housekeeper.photoVerifyUrl,
            detailStatusVerify = housekeeper.statusVerify,
            detailRating = housekeeper.rating,
            detailDailyRate = housekeeper.dailyRate?.toDoubleOrNull(),
            detailHousekeeperSkills = housekeeper.housekeeperSkills?.map { hs ->
                HousekeeperSkillDTO(
                    id = hs.id,
                    housekeeperId = hs.housekeeper?.id?.toInt(),
                    skillTypeId = hs.skillType?.id?.toInt(),
                    skillLevelTierId = hs.skillLevelTier?.id?.toInt(),
                    pricePerDay = hs.pricePerDay,
                    totalHiresCompleted = hs.totalHiresCompleted
                )
            }?.toSet(),
            hires = housekeeper.hires?.map { hire -> hire.toHireDTO() },
            reviews = null
        )
    }

    private fun buildFullImageUrl(filename: String?, folderName: String): String? {
        if (filename.isNullOrEmpty()) return null
        if (filename.startsWith("http://") || filename.startsWith("https://")) return filename
        return "$publicBaseUrl/maeban/files/download/$folderName/$filename"
    }

    private fun transformHousekeeperUrls(housekeeper: Housekeeper?): Housekeeper? {
        if (housekeeper == null) return null
        housekeeper.photoVerifyUrl = buildFullImageUrl(housekeeper.photoVerifyUrl, "verify_photos")
        housekeeper.person?.let { person ->
            person.pictureUrl = buildFullImageUrl(person.pictureUrl, "profile_pictures")
        }
        return housekeeper
    }

    private fun transformHireHirerUrls(hires: List<HireDTO>?) {
        if (hires == null) return
        for (hireDto in hires) {
            hireDto.hirer?.person?.let { hirerPersonDto ->
                val originalFilename = hirerPersonDto.pictureUrl
                hirerPersonDto.pictureUrl = buildFullImageUrl(originalFilename, "profile_pictures")
            }
        }
    }

    @Transactional(readOnly = true)
    override fun getAllHousekeepers(): List<HousekeeperDTO> {
        val entities = housekeeperRepository.findAllWithPersonAndSkills()
        return entities
            .map { transformHousekeeperUrls(it) }
            .mapNotNull { it?.let { hk -> mapHousekeeperToDto(hk) } }
    }

    @Transactional(readOnly = true)
    override fun getHousekeeperDetailById(id: Long): HousekeeperDetailDTO? {
        val housekeeperOptional = housekeeperRepository.findByIdWithAllDetails(id)
        if (housekeeperOptional.isEmpty) return null

        val housekeeper = housekeeperOptional.get()
        val transformedHousekeeper = transformHousekeeperUrls(housekeeper)
        val detailDto = mapHousekeeperToDetailDto(transformedHousekeeper!!)

        detailDto.hires?.let { transformHireHirerUrls(it) }

        val reviews = detailDto.hires
            ?.mapNotNull { it.review }
            ?: emptyList()

        detailDto.reviews = reviews
        detailDto.hires = null

        return detailDto
    }

    @Transactional
    override fun saveHousekeeper(housekeeperDto: HousekeeperDTO): HousekeeperDTO {
        val housekeeper = Housekeeper().apply {
            balance = housekeeperDto.balance
            photoVerifyUrl = housekeeperDto.photoVerifyUrl
            statusVerify = housekeeperDto.statusVerify
            rating = housekeeperDto.rating
            dailyRate = housekeeperDto.dailyRate
        }

        if (housekeeper.statusVerify == null) {
            housekeeper.statusVerify = Housekeeper.VerifyStatus.PENDING.name
        }

        val savedHousekeeper = housekeeperRepository.save(housekeeper)
        val transformedHousekeeper = transformHousekeeperUrls(savedHousekeeper)

        return mapHousekeeperToDto(transformedHousekeeper!!)
    }

    @Transactional
    override fun updateHousekeeper(id: Long, housekeeperDto: HousekeeperDTO): HousekeeperDTO {
        val existingHousekeeper = housekeeperRepository.findById(id)
            .orElseThrow { RuntimeException("Housekeeper with ID $id not found.") }

        if (housekeeperDto.person != null && existingHousekeeper.person != null) {
            val existingPerson = existingHousekeeper.person!!
            existingPerson.email = housekeeperDto.person?.email
            existingPerson.firstName = housekeeperDto.person?.firstName
            existingPerson.lastName = housekeeperDto.person?.lastName
            existingPerson.phoneNumber = housekeeperDto.person?.phoneNumber
            existingPerson.address = housekeeperDto.person?.address
            existingPerson.accountStatus = housekeeperDto.person?.accountStatus
            personRepository.save(existingPerson)
        }

        housekeeperDto.statusVerify?.let {
            existingHousekeeper.statusVerify = it
        }
        existingHousekeeper.dailyRate = housekeeperDto.dailyRate

        val updatedHousekeeper = housekeeperRepository.save(existingHousekeeper)
        val transformedHousekeeper = transformHousekeeperUrls(updatedHousekeeper)

        return mapHousekeeperToDto(transformedHousekeeper!!)
    }

    @Transactional
    override fun deleteHousekeeper(id: Long) {
        housekeeperRepository.deleteById(id)
    }

    @Transactional
    override fun calculateAndSetAverageRating(housekeeperId: Long) {
        val housekeeperOptional = housekeeperRepository.findById(housekeeperId)

        if (housekeeperOptional.isPresent) {
            val housekeeper = housekeeperOptional.get()
            var averageRating = housekeeperRepository.calculateAverageRatingByHousekeeperId(housekeeperId)

            if (averageRating == null) {
                averageRating = 0.0
            }
            housekeeper.rating = averageRating

            housekeeperRepository.save(housekeeper)
            println("Housekeeper ID: ${housekeeper.id} - Average Rating updated to: ${String.format("%.2f", averageRating)}")
        } else {
            System.err.println("Housekeeper with ID $housekeeperId not found for rating calculation.")
        }
    }

    @Transactional
    override fun addBalance(housekeeperId: Long, amount: Double) {
        val housekeeper = housekeeperRepository.findById(housekeeperId)
            .orElseThrow { RuntimeException("Housekeeper with ID $housekeeperId not found.") }

        val currentBalance = housekeeper.balance ?: 0.0
        housekeeper.balance = currentBalance + amount
        housekeeperRepository.save(housekeeper)
        println("Balance added to housekeeper $housekeeperId: $amount. New balance: ${housekeeper.balance}")
    }

    @Transactional
    override fun deductBalance(housekeeperId: Long, amount: Double) {
        val housekeeper = housekeeperRepository.findById(housekeeperId)
            .orElseThrow { RuntimeException("Housekeeper with ID $housekeeperId not found.") }

        val currentBalance = housekeeper.balance ?: 0.0
        if (currentBalance < amount) {
            throw IllegalStateException("Housekeeper balance is insufficient for deduction.")
        }
        housekeeper.balance = currentBalance - amount
        housekeeperRepository.save(housekeeper)
        println("Balance deducted from housekeeper $housekeeperId: $amount. New balance: ${housekeeper.balance}")
    }

    @Transactional(readOnly = true)
    override fun getHousekeepersByStatus(status: String): List<HousekeeperDTO> {
        val entities = housekeeperRepository.findByStatusVerifyWithDetails(status)
        return entities
            .map { transformHousekeeperUrls(it) }
            .mapNotNull { it?.let { hk -> mapHousekeeperToDto(hk) } }
    }

    @Transactional(readOnly = true)
    override fun getNotVerifiedOrNullStatusHousekeepers(): List<HousekeeperDTO> {
        val entities = housekeeperRepository.findNotVerifiedOrNullStatusHousekeepersWithDetails()
        return entities
            .map { transformHousekeeperUrls(it) }
            .mapNotNull { it?.let { hk -> mapHousekeeperToDto(hk) } }
    }
}

