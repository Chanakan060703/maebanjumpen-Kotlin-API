package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.entity.AccountManager
import com.itsci.mju.maebanjumpen.partyrole.dto.AccountManagerDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.AccountManagerRepository
import com.itsci.mju.maebanjumpen.partyrole.service.AccountManagerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountManagerServiceImpl @Autowired internal constructor(
    private val accountManagerRepository: AccountManagerRepository
) : AccountManagerService {

    private fun mapAccountManagerToDto(am: AccountManager): AccountManagerDTO {
        return AccountManagerDTO().apply {
            id = am.id
        }
    }

    override fun listAllAccountManagers(): List<AccountManagerDTO> {
        return accountManagerRepository.findAll().map { mapAccountManagerToDto(it) }
    }

    override fun getAccountManagerById(id: Long): AccountManagerDTO {
        val entity = accountManagerRepository.findById(id)
            .orElseThrow { NoSuchElementException("Account Manager not found with ID: $id") }
        return mapAccountManagerToDto(entity)
    }

    override fun createAccountManager(accountManagerDto: AccountManagerDTO): AccountManagerDTO {
        val entity = AccountManager()
        val savedEntity = accountManagerRepository.save(entity)
        return mapAccountManagerToDto(savedEntity)
    }

    override fun updateAccountManager(id: Long, accountManagerDto: AccountManagerDTO): AccountManagerDTO {
        val existingAccountManager = accountManagerRepository.findById(id)
            .orElseThrow { NoSuchElementException("Account Manager not found with ID: $id") }
        val updatedEntity = accountManagerRepository.save(existingAccountManager)
        return mapAccountManagerToDto(updatedEntity)
    }

    override fun deleteAccountManager(id: Long) {
        if (!accountManagerRepository.existsById(id)) {
            throw NoSuchElementException("Account Manager not found with ID: $id")
        }
        accountManagerRepository.deleteById(id)
    }
}

