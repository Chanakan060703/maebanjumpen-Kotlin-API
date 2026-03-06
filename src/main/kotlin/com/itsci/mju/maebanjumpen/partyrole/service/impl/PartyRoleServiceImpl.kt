package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.entity.*
import com.itsci.mju.maebanjumpen.partyrole.dto.*
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository
import com.itsci.mju.maebanjumpen.partyrole.service.PartyRoleService
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartyRoleServiceImpl(
    private val partyRoleRepository: PartyRoleRepository,
    private val personRepository: PersonRepository
) : PartyRoleService {

    private fun mapPartyRoleToDto(partyRole: PartyRole): PartyRoleDTO {
        return when (partyRole) {
            is Hirer -> HirerDTO().apply {
                id = partyRole.id
                balance = partyRole.balance
            }
            is Housekeeper -> HousekeeperDTO().apply {
                id = partyRole.id
                balance = partyRole.balance
                photoVerifyUrl = partyRole.photoVerifyUrl
                statusVerify = partyRole.statusVerify
                rating = partyRole.rating
                dailyRate = partyRole.dailyRate
            }
            is Admin -> AdminDTO().apply {
                id = partyRole.id
            }
            is AccountManager -> AccountManagerDTO().apply {
                id = partyRole.id
                managerID = partyRole.id
            }
            is Member -> MemberDTO().apply {
                id = partyRole.id
                balance = partyRole.balance
            }
            else -> throw IllegalArgumentException("Unknown PartyRole type: ${partyRole::class.simpleName}")
        }
    }

    @Transactional
    override fun savePartyRole(partyRoleDto: PartyRoleDTO): PartyRoleDTO {
        if (partyRoleDto.person?.id == null) {
            throw IllegalArgumentException("Person ID is required to create a PartyRole.")
        }

        val personId = partyRoleDto.person!!.id!!
        val existingPerson = personRepository.findById(personId)
            .orElseThrow { RuntimeException("Person ID: $personId not found. Failed to link PartyRole.") }

        val partyRole = when (partyRoleDto) {
            is HirerDTO -> Hirer().apply { balance = partyRoleDto.balance }
            is HousekeeperDTO -> Housekeeper().apply {
                balance = partyRoleDto.balance
                photoVerifyUrl = partyRoleDto.photoVerifyUrl
                statusVerify = partyRoleDto.statusVerify
                rating = partyRoleDto.rating
                dailyRate = partyRoleDto.dailyRate
            }
            is AdminDTO -> Admin()
            is AccountManagerDTO -> AccountManager()
            is MemberDTO -> Member().apply { balance = partyRoleDto.balance }
            else -> throw IllegalArgumentException("Unknown PartyRoleDTO type: ${partyRoleDto::class.simpleName}")
        }
        partyRole.person = existingPerson

        val savedPartyRole = partyRoleRepository.save(partyRole)
        return mapPartyRoleToDto(savedPartyRole)
    }

    @Transactional(readOnly = true)
    override fun getPartyRoleById(id: Long): PartyRoleDTO? {
        return partyRoleRepository.findById(id)
            .map { mapPartyRoleToDto(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun getAllPartyRoles(): List<PartyRoleDTO> {
        val partyRoles = partyRoleRepository.findAll()
        return partyRoles.map { mapPartyRoleToDto(it) }
    }

    @Transactional
    override fun updatePartyRole(id: Long, partyRoleDto: PartyRoleDTO): PartyRoleDTO {
        return partyRoleRepository.findById(id).map { existingPartyRole ->
            // Update logic can be added here based on type
            val savedRole = partyRoleRepository.save(existingPartyRole)
            mapPartyRoleToDto(savedRole)
        }.orElseThrow { RuntimeException("PartyRole not found with ID: $id") }
    }

    @Transactional
    override fun deletePartyRole(id: Long) {
        partyRoleRepository.deleteById(id)
    }
}

