package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.entity.Hirer
import com.itsci.mju.maebanjumpen.partyrole.dto.HirerDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.HirerRepository
import com.itsci.mju.maebanjumpen.partyrole.service.HirerService
import org.hibernate.Hibernate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HirerServiceImpl @Autowired internal constructor(
    private val hirerRepository: HirerRepository
) : HirerService {

    private fun mapHirerToDto(hirer: Hirer): HirerDTO {
        return HirerDTO().apply {
            id = hirer.id
            balance = hirer.balance
        }
    }

    private fun initializeHirerDetails(hirer: Hirer?) {
        if (hirer == null) return

        hirer.person?.let { person ->
            Hibernate.initialize(person)
        }

        hirer.transactions?.let { transactions ->
            Hibernate.initialize(transactions)
        }

        hirer.hires?.let { hires ->
            Hibernate.initialize(hires)
            for (hire in hires) {
                hire.review?.let { Hibernate.initialize(it) }
                hire.housekeeper?.let { housekeeper ->
                    Hibernate.initialize(housekeeper)
                    housekeeper.person?.let { person ->
                        Hibernate.initialize(person)
                    }
                    housekeeper.housekeeperSkills?.let { skills ->
                        Hibernate.initialize(skills)
                        for (skill in skills) {
                            skill.skillType?.let { Hibernate.initialize(it) }
                        }
                    }
                }
            }
        }
    }

    @Transactional
    override fun saveHirer(hirerDto: HirerDTO): HirerDTO {
        val hirerToSave = Hirer().apply { balance = hirerDto.balance }
        val savedHirer = hirerRepository.save(hirerToSave)
        initializeHirerDetails(savedHirer)
        return mapHirerToDto(savedHirer)
    }

    @Transactional(readOnly = true)
    override fun getHirerById(id: Long): HirerDTO {
        val hirer = hirerRepository.findById(id)
            .orElseThrow { RuntimeException("Hirer not found with ID: $id") }
        initializeHirerDetails(hirer)
        return mapHirerToDto(hirer)
    }

    @Transactional(readOnly = true)
    override fun getAllHirers(): List<HirerDTO> {
        val hirers = hirerRepository.findAll()
        for (hirer in hirers) {
            initializeHirerDetails(hirer)
        }
        return hirers.map { mapHirerToDto(it) }
    }

    @Transactional
    override fun updateHirer(id: Long, hirerDto: HirerDTO): HirerDTO {
        val existingHirer = hirerRepository.findById(id)
            .orElseThrow { RuntimeException("Hirer not found with ID: $id") }

        existingHirer.balance = hirerDto.balance

        if (existingHirer.person != null && hirerDto.person != null) {
            val existingPerson = existingHirer.person!!
            existingPerson.email = hirerDto.person?.email
            existingPerson.firstName = hirerDto.person?.firstName
            existingPerson.lastName = hirerDto.person?.lastName
            existingPerson.idCardNumber = hirerDto.person?.idCardNumber
            existingPerson.phoneNumber = hirerDto.person?.phoneNumber
            existingPerson.address = hirerDto.person?.address
            existingPerson.pictureUrl = hirerDto.person?.pictureUrl
            existingPerson.accountStatus = hirerDto.person?.accountStatus
        }

        val updatedHirer = hirerRepository.save(existingHirer)
        initializeHirerDetails(updatedHirer)
        return mapHirerToDto(updatedHirer)
    }

    @Transactional
    override fun deleteHirer(id: Long) {
        if (!hirerRepository.existsById(id)) {
            throw RuntimeException("Hirer with ID: $id not found for deletion.")
        }
        hirerRepository.deleteById(id)
    }

    @Transactional
    override fun deductBalance(hirerId: Long, amount: Double) {
        val hirer = hirerRepository.findById(hirerId)
            .orElseThrow { RuntimeException("Hirer with ID $hirerId not found.") }

        val currentBalance = hirer.balance ?: 0.0
        if (currentBalance < amount) {
            throw RuntimeException("Insufficient balance for hirer ID: $hirerId. Required: $amount, Available: $currentBalance")
        }
        hirer.balance = currentBalance - amount
        hirerRepository.save(hirer)
    }

    @Transactional
    override fun addBalance(hirerId: Long, amount: Double) {
        val hirer = hirerRepository.findById(hirerId)
            .orElseThrow { RuntimeException("Hirer with ID $hirerId not found.") }

        val currentBalance = hirer.balance ?: 0.0
        hirer.balance = currentBalance + amount
        hirerRepository.save(hirer)
    }
}

