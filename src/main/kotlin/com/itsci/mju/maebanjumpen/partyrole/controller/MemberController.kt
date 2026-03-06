package com.itsci.mju.maebanjumpen.partyrole.controller

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import com.itsci.mju.maebanjumpen.partyrole.service.MemberService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/members")
class MemberController @Autowired internal constructor(
    private val memberService: MemberService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getAllMembers(): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ member สำเร็จ",
                    memberService.getAllMembers()
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ member ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/{id}")
    fun getMemberById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val member = memberService.getMemberById(id)
            if (member.isPresent) {
                ResponseEntity.ok().body(
                    HttpResponse(
                        true,
                        "ดึงข้อมูล member สำเร็จ",
                        member.get()
                    )
                )
            } else {
                ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ไม่พบข้อมูล member"
                    )
                )
            }
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล member"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ดึงข้อมูล member ไม่สำเร็จ"
                )
            )
        }
    }

    @PostMapping
    fun createMember(@Valid @RequestBody member: MemberDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "สร้าง member สำเร็จ",
                    memberService.saveMember(member)
                )
            )
        } catch (e: BadRequestException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ส่งคำขอสร้าง member ไม่ถูกต้อง",
                    false
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "สร้าง member ไม่สำเร็จ",
                    false
                )
            )
        }
    }

    @PutMapping("/{id}")
    fun updateMember(@PathVariable id: Long, @Valid @RequestBody memberDetails: MemberDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "อัพเดท member สำเร็จ",
                    memberService.updateMember(id, memberDetails)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล member"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "อัพเดท member ไม่สำเร็จ"
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deleteMember(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            memberService.deleteMember(id)
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "ลบ member สำเร็จ"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ลบ member ไม่สำเร็จ"
                )
            )
        }
    }
}

