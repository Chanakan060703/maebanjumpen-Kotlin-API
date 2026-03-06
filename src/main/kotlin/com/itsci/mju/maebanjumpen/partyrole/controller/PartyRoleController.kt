package com.itsci.mju.maebanjumpen.partyrole.controller

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO
import com.itsci.mju.maebanjumpen.partyrole.service.PartyRoleService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/party-roles")
class PartyRoleController @Autowired internal constructor(
    private val partyRoleService: PartyRoleService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getAllPartyRole(): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ party role สำเร็จ",
                    partyRoleService.getAllPartyRoles()
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ party role ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/{id}")
    fun getPartyRole(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val partyRole = partyRoleService.getPartyRoleById(id)
            if (partyRole != null) {
                ResponseEntity.ok().body(
                    HttpResponse(
                        true,
                        "ดึงข้อมูล party role สำเร็จ",
                        partyRole
                    )
                )
            } else {
                ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ไม่พบข้อมูล party role"
                    )
                )
            }
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล party role"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ดึงข้อมูล party role ไม่สำเร็จ"
                )
            )
        }
    }

    @PostMapping
    fun createPartyRole(@Valid @RequestBody partyRoleDto: PartyRoleDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "สร้าง party role สำเร็จ",
                    partyRoleService.savePartyRole(partyRoleDto)
                )
            )
        } catch (e: BadRequestException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ส่งคำขอสร้าง party role ไม่ถูกต้อง",
                    false
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "สร้าง party role ไม่สำเร็จ",
                    false
                )
            )
        }
    }

    @PutMapping("/{id}")
    fun updatePartyRole(@PathVariable id: Long, @Valid @RequestBody partyRoleDto: PartyRoleDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "อัพเดท party role สำเร็จ",
                    partyRoleService.updatePartyRole(id, partyRoleDto)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล party role"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "อัพเดท party role ไม่สำเร็จ"
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deletePartyRole(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            partyRoleService.deletePartyRole(id)
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "ลบ party role สำเร็จ"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ลบ party role ไม่สำเร็จ"
                )
            )
        }
    }
}

