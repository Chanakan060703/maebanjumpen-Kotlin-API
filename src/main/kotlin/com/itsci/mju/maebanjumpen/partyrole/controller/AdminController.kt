package com.itsci.mju.maebanjumpen.partyrole.controller

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.partyrole.dto.AdminDTO
import com.itsci.mju.maebanjumpen.partyrole.service.AdminService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/admins")
class AdminController @Autowired internal constructor(
    private val adminService: AdminService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getAllAdmins(): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ admin สำเร็จ",
                    adminService.listAllAdmins()
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ admin ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/{id}")
    fun getAdminById(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "ดึงข้อมูล admin สำเร็จ",
                    adminService.getAdminById(id)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล admin"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ดึงข้อมูล admin ไม่สำเร็จ"
                )
            )
        }
    }

    @PostMapping
    fun createAdmin(@Valid @RequestBody admin: AdminDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "สร้าง admin สำเร็จ",
                    adminService.createAdmin(admin)
                )
            )
        } catch (e: BadRequestException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ส่งคำขอสร้าง admin ไม่ถูกต้อง",
                    false
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "สร้าง admin ไม่สำเร็จ",
                    false
                )
            )
        }
    }

    @PutMapping("/{id}")
    fun updateAdmin(@PathVariable id: Int, @Valid @RequestBody admin: AdminDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "อัพเดท admin สำเร็จ",
                    adminService.updateAdmin(id, admin)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล admin"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "อัพเดท admin ไม่สำเร็จ"
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deleteAdmin(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            adminService.deleteAdmin(id)
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "ลบ admin สำเร็จ"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ลบ admin ไม่สำเร็จ"
                )
            )
        }
    }
}

