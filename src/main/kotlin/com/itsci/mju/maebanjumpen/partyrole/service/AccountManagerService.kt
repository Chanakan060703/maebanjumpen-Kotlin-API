package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.partyrole.dto.AccountManagerDTO

interface AccountManagerService {
    fun listAllAccountManagers(): List<AccountManagerDTO>
    fun getAccountManagerById(id: Long): AccountManagerDTO
    fun createAccountManager(accountManager: AccountManagerDTO): AccountManagerDTO
    fun deleteAccountManager(id: Long)
    fun updateAccountManager(id: Long, accountManager: AccountManagerDTO): AccountManagerDTO
}

