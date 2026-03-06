package com.itsci.mju.maebanjumpen.partyrole.controller

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.partyrole.dto.AccountManagerDTO
import com.itsci.mju.maebanjumpen.partyrole.service.AccountManagerService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/account-managers")
class AccountManagerController @Autowired internal constructor(
    private val accountManagerService: AccountManagerService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getAllAccountManagers(): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ account manager สำเร็จ",
                    accountManagerService.listAllAccountManagers()
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ account manager ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/{id}")
    fun getAccountManagerById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "ดึงข้อมูล account manager สำเร็จ",
                    accountManagerService.getAccountManagerById(id)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล account manager"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ดึงข้อมูล account manager ไม่สำเร็จ"
                )
            )
        }
    }

    @PostMapping
    fun createAccountManager(@Valid @RequestBody accountManager: AccountManagerDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "สร้าง account manager สำเร็จ",
                    accountManagerService.createAccountManager(accountManager)
                )
            )
        } catch (e: BadRequestException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ส่งคำขอสร้าง account manager ไม่ถูกต้อง",
                    false
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "สร้าง account manager ไม่สำเร็จ",
                    false
                )
            )
        }
    }

    @PutMapping("/{id}")
    fun updateAccountManager(@PathVariable id: Long, @Valid @RequestBody accountManager: AccountManagerDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "อัพเดท account manager สำเร็จ",
                    accountManagerService.updateAccountManager(id, accountManager)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล account manager"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "อัพเดท account manager ไม่สำเร็จ"
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deleteAccountManager(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            accountManagerService.deleteAccountManager(id)
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "ลบ account manager สำเร็จ"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ลบ account manager ไม่สำเร็จ"
                )
            )
        }
    }
}

