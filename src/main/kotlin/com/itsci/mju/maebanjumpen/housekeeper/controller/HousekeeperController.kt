package com.itsci.mju.maebanjumpen.housekeeper.controller

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.housekeeper.dto.HousekeeperDetailDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import com.itsci.mju.maebanjumpen.partyrole.service.HousekeeperService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/housekeepers")
class HousekeeperController @Autowired internal constructor(
    private val housekeeperService: HousekeeperService) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getAllHousekeepers(
    ): ResponseEntity<Any> {
       return try {
            ResponseEntity.ok()
                .body(
                    HttpResponse(
                        true,
                        "รายการ housekeeper สำเร็จ",
                        housekeeperService.getAllHousekeepers()
                    )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ housekeeper ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/{id}")
    fun getHousekeeperDetailById(
        @PathVariable id: Long
    ): ResponseEntity<Any> {
        return try {
            val housekeeper = housekeeperService.getHousekeeperDetailById(id)
            if (housekeeper == null) {
                ResponseEntity.notFound().build()
            } else {
                ResponseEntity.ok(housekeeper)
            }
        }
        catch (e: NotFoundException) {
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ housekeeper ไม่พบข้อมูล"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ housekeeper ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/status/{status}")
    fun getHousekeepersByStatus(@PathVariable status: String): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ housekeeper ตามสถานะ สำเร็จ",
                    housekeeperService.getHousekeepersByStatus(status)
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ housekeeper ตามสถานะ ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/unverified-or-null")
    fun getUnverifiedOrNullStatusHousekeepers(): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ housekeeper ที่ยังไม่ยืนยัน สำเร็จ",
                    housekeeperService.getNotVerifiedOrNullStatusHousekeepers()
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ housekeeper ที่ยังไม่ยืนยัน ไม่สำเร็จ"
                )
            )
        }
    }

    @PostMapping
    fun createHousekeeper(
        @Valid @RequestBody housekeeper: HousekeeperDTO
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok()
                .body(
                    HttpResponse(
                        true,
                        "สร้าง housekeeper สำเร็จ",
                        housekeeperService.saveHousekeeper(housekeeper)
                    )
                )
        } catch (e: BadRequestException) {
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ส่งคำขอสร้าง housekeeper ไม่ถูกต้อง",
                    false
                )
            )
        }  catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "สร้าง housekeeper ไม่สำเร็จ",
                    false
                )
            )
        }
    }

    @PutMapping("/{id}")
    fun updateHousekeeper(@PathVariable id: Long, @Valid @RequestBody housekeeper: HousekeeperDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "อัพเดท housekeeper สำเร็จ",
                    housekeeperService.updateHousekeeper(id, housekeeper)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล housekeeper"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "อัพเดท housekeeper ไม่สำเร็จ"
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deleteHousekeeper(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            housekeeperService.deleteHousekeeper(id)
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "ลบ housekeeper สำเร็จ"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ลบ housekeeper ไม่สำเร็จ"
                )
            )
        }
    }
}

