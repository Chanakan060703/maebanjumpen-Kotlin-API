package com.itsci.mju.maebanjumpen.housekeeper.controller

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.housekeeper.dto.HousekeeperSkillDTO
import com.itsci.mju.maebanjumpen.housekeeper.service.HousekeeperSkillService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/housekeeper-skills")
class HousekeeperSkillController @Autowired internal constructor(
    private val housekeeperSkillService: HousekeeperSkillService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getAllHousekeeperSkills(): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ housekeeper skill สำเร็จ",
                    housekeeperSkillService.getAllHousekeeperSkills()
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ housekeeper skill ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/{id}")
    fun getHousekeeperSkillById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val skill = housekeeperSkillService.getHousekeeperSkillById(id)
            if (skill != null) {
                ResponseEntity.ok().body(
                    HttpResponse(
                        true,
                        "ดึงข้อมูล housekeeper skill สำเร็จ",
                        skill
                    )
                )
            } else {
                ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ไม่พบข้อมูล housekeeper skill"
                    )
                )
            }
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล housekeeper skill"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ดึงข้อมูล housekeeper skill ไม่สำเร็จ"
                )
            )
        }
    }

    @PostMapping
    fun createHousekeeperSkill(@Valid @RequestBody housekeeperSkillDto: HousekeeperSkillDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "สร้าง housekeeper skill สำเร็จ",
                    housekeeperSkillService.saveHousekeeperSkill(housekeeperSkillDto)
                )
            )
        } catch (e: BadRequestException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ส่งคำขอสร้าง housekeeper skill ไม่ถูกต้อง",
                    false
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "สร้าง housekeeper skill ไม่สำเร็จ",
                    false
                )
            )
        }
    }

    @PutMapping("/{id}")
    fun updateHousekeeperSkill(@PathVariable id: Long, @Valid @RequestBody skillDto: HousekeeperSkillDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "อัพเดท housekeeper skill สำเร็จ",
                    housekeeperSkillService.updateHousekeeperSkill(id, skillDto)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล housekeeper skill"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "อัพเดท housekeeper skill ไม่สำเร็จ"
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deleteHousekeeperSkill(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            if (housekeeperSkillService.getHousekeeperSkillById(id) == null) {
                ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ไม่พบข้อมูล housekeeper skill ที่ต้องการลบ"
                    )
                )
            } else {
                housekeeperSkillService.deleteHousekeeperSkill(id)
                ResponseEntity.ok().body(
                    HttpResponse(
                        true,
                        "ลบ housekeeper skill สำเร็จ"
                    )
                )
            }
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ลบ housekeeper skill ไม่สำเร็จ"
                )
            )
        }
    }
}

