package com.itsci.mju.maebanjumpen.partyrole.controller

import com.itsci.mju.maebanjumpen.partyrole.dto.AccountManagerDTO
import com.itsci.mju.maebanjumpen.partyrole.service.AccountManagerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/account-managers")
class AccountManagerController(private val accountManagerService: AccountManagerService) {

    @GetMapping
    fun getAllAccountManagers(): ResponseEntity<List<AccountManagerDTO>> {
        val accountManagers = accountManagerService.listAllAccountManagers()
        return ResponseEntity.ok(accountManagers)
    }

    @GetMapping("/{id}")
    fun getAccountManagerById(@PathVariable id: Long): ResponseEntity<AccountManagerDTO> {
        val accountManager = accountManagerService.getAccountManagerById(id)
        return ResponseEntity.ok(accountManager)
    }

    @PostMapping
    fun createAccountManager(@RequestBody accountManager: AccountManagerDTO): ResponseEntity<AccountManagerDTO> {
        val savedAccountManager = accountManagerService.createAccountManager(accountManager)
        return ResponseEntity.ok(savedAccountManager)
    }

    @PutMapping("/{id}")
    fun updateAccountManager(@PathVariable id: Long, @RequestBody accountManager: AccountManagerDTO): ResponseEntity<AccountManagerDTO> {
        val updatedAccountManager = accountManagerService.updateAccountManager(id, accountManager)
        return ResponseEntity.ok(updatedAccountManager)
    }

    @DeleteMapping("/{id}")
    fun deleteAccountManager(@PathVariable id: Long): ResponseEntity<Void> {
        accountManagerService.deleteAccountManager(id)
        return ResponseEntity.noContent().build()
    }
}

