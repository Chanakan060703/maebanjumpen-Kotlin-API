package com.itsci.mju.maebanjumpen.penalty.controller

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO
import com.itsci.mju.maebanjumpen.penalty.service.PenaltyService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/penalties")
class PenaltyController @Autowired internal constructor(
    private val penaltyService: PenaltyService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun createPenalty(
        @Valid @RequestBody penaltyDTO: PenaltyDTO,
        @RequestParam("reportId") reportId: Long,
        @RequestParam(value = "hirerId", required = false) hirerId: Long?,
        @RequestParam(value = "housekeeperId", required = false) housekeeperId: Long?
    ): ResponseEntity<Any> {
        val targetRoleId: Long? = when {
            hirerId != null -> hirerId
            housekeeperId != null -> housekeeperId
            else -> {
                logger.error("Missing target Role ID (hirerId or housekeeperId) for penalty creation.")
                return ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ต้องระบุ hirerId หรือ housekeeperId"
                    )
                )
            }
        }

        penaltyDTO.reportId = reportId

        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "สร้าง penalty สำเร็จ",
                    penaltyService.savePenalty(penaltyDTO, targetRoleId!!)
                )
            )
        } catch (e: BadRequestException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ส่งคำขอสร้าง penalty ไม่ถูกต้อง",
                    false
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูลที่เกี่ยวข้อง"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "สร้าง penalty ไม่สำเร็จ",
                    false
                )
            )
        }
    }

    @GetMapping
    fun getAllPenalties(): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ penalty สำเร็จ",
                    penaltyService.getAllPenalties()
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ penalty ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/{id}")
    fun getPenaltyById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val penalty = penaltyService.getPenaltyById(id)
            if (penalty != null) {
                ResponseEntity.ok().body(
                    HttpResponse(
                        true,
                        "ดึงข้อมูล penalty สำเร็จ",
                        penalty
                    )
                )
            } else {
                ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ไม่พบข้อมูล penalty"
                    )
                )
            }
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล penalty"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ดึงข้อมูล penalty ไม่สำเร็จ"
                )
            )
        }
    }

    @PutMapping("/{id}")
    fun updatePenalty(@PathVariable id: Long, @Valid @RequestBody penaltyDTO: PenaltyDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "อัพเดท penalty สำเร็จ",
                    penaltyService.updatePenalty(id, penaltyDTO)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล penalty"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "อัพเดท penalty ไม่สำเร็จ"
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deletePenalty(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            penaltyService.deletePenalty(id)
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "ลบ penalty สำเร็จ"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ลบ penalty ไม่สำเร็จ"
                )
            )
        }
    }
}

