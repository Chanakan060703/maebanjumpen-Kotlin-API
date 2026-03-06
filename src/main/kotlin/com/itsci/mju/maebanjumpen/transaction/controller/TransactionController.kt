package com.itsci.mju.maebanjumpen.transaction.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import com.itsci.mju.maebanjumpen.partyrole.service.MemberService
import com.itsci.mju.maebanjumpen.transaction.constant.TransactionStatusEnum
import com.itsci.mju.maebanjumpen.transaction.dto.QrCodeRequestDTO
import com.itsci.mju.maebanjumpen.transaction.dto.TransactionDTO
import com.itsci.mju.maebanjumpen.transaction.service.OmiseService
import com.itsci.mju.maebanjumpen.transaction.service.TransactionService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban")
class TransactionController @Autowired internal constructor(
    private val transactionService: TransactionService,
    private val memberService: MemberService,
    private val omiseService: OmiseService,
    private val objectMapper: ObjectMapper
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/transactions")
    fun getAllTransactions(): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ transaction สำเร็จ",
                    transactionService.getAllTransactions()
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ transaction ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/transactions/{id}")
    fun getTransactionById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val transaction = transactionService.getTransactionById(id)
            if (transaction.isPresent) {
                ResponseEntity.ok().body(
                    HttpResponse(
                        true,
                        "ดึงข้อมูล transaction สำเร็จ",
                        transaction.get()
                    )
                )
            } else {
                ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ไม่พบข้อมูล transaction"
                    )
                )
            }
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล transaction"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ดึงข้อมูล transaction ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping(value = ["/transactions"], params = ["memberId"])
    fun getTransactionsByMemberId(@RequestParam memberId: Long): ResponseEntity<Any> {
        return try {
            val transactions = transactionService.getTransactionsByMemberId(memberId)
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "รายการ transaction ตาม member สำเร็จ",
                    transactions
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "รายการ transaction ตาม member ไม่สำเร็จ"
                )
            )
        }
    }

    @GetMapping("/transactions/{transactionId}/status")
    fun getTransactionStatus(@PathVariable transactionId: Long): ResponseEntity<Any> {
        return try {
            val optionalTransaction = transactionService.getTransactionById(transactionId)
            if (optionalTransaction.isPresent) {
                val transaction = optionalTransaction.get()
                ResponseEntity.ok().body(
                    HttpResponse(
                        true,
                        "ดึงสถานะ transaction สำเร็จ",
                        mapOf(
                            "transactionId" to transaction.transactionId.toString(),
                            "transactionStatus" to (transaction.transactionStatus ?: "")
                        )
                    )
                )
            } else {
                ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ไม่พบข้อมูล transaction"
                    )
                )
            }
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ดึงสถานะ transaction ไม่สำเร็จ"
                )
            )
        }
    }

    @PostMapping("/transactions")
    fun createTransaction(@Valid @RequestBody transactionDto: TransactionDTO): ResponseEntity<Any> {
        return try {
            if (transactionDto.member?.id == null) {
                return ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "Member ID is missing or invalid in the request."
                    )
                )
            }

            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "สร้าง transaction สำเร็จ",
                    transactionService.saveTransaction(transactionDto)
                )
            )
        } catch (e: BadRequestException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ส่งคำขอสร้าง transaction ไม่ถูกต้อง",
                    false
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "สร้าง transaction ไม่สำเร็จ",
                    false
                )
            )
        }
    }

    @PatchMapping("/transactions/{transactionId}/status")
    fun updateTransactionStatus(
        @PathVariable transactionId: Long,
        @RequestBody requestBody: Map<String, String>
    ): ResponseEntity<Any> {
        return try {
            val newStatus = requestBody["newStatus"]

            if (newStatus.isNullOrEmpty()) {
                return ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "New status is required."
                    )
                )
            }

            val updatedTransaction = transactionService.updateWithdrawalRequestStatus(transactionId, newStatus)

            if (updatedTransaction.isEmpty) {
                ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ไม่พบข้อมูล transaction"
                    )
                )
            } else {
                ResponseEntity.ok().body(
                    HttpResponse(
                        true,
                        "อัพเดทสถานะ transaction สำเร็จ",
                        mapOf(
                            "transactionId" to updatedTransaction.get().transactionId.toString(),
                            "newStatus" to (updatedTransaction.get().transactionStatus ?: "")
                        )
                    )
                )
            }
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "อัพเดทสถานะ transaction ไม่สำเร็จ"
                )
            )
        }
    }

    @PutMapping("/transactions/{id}")
    fun updateTransaction(@PathVariable id: Long, @Valid @RequestBody transactionDto: TransactionDTO): ResponseEntity<Any> {
        return try {
            if (transactionService.getTransactionById(id).isEmpty) {
                return ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "ไม่พบข้อมูล transaction"
                    )
                )
            }
            transactionDto.transactionId = id
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "อัพเดท transaction สำเร็จ",
                    transactionService.saveTransaction(transactionDto)
                )
            )
        } catch (e: NotFoundException) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ไม่พบข้อมูล transaction"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "อัพเดท transaction ไม่สำเร็จ"
                )
            )
        }
    }

    @DeleteMapping("/transactions/{id}")
    fun deleteTransaction(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            transactionService.deleteTransaction(id)
            ResponseEntity.ok().body(
                HttpResponse(
                    true,
                    "ลบ transaction สำเร็จ"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ลบ transaction ไม่สำเร็จ"
                )
            )
        }
    }

    @PostMapping("/transactions/qrcode/deposit")
    fun createDepositQrCode(@RequestBody request: QrCodeRequestDTO): ResponseEntity<Any> {
        val memberId = request.memberId
        val amount = request.amount

        if (memberId == null) {
            return ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    "Member ID is missing or invalid in the request body."
                )
            )
        }
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    "A positive amount is required."
                )
            )
        }

        var depositTransactionDto: TransactionDTO? = null
        var savedTransactionDto: TransactionDTO? = null

        return try {
            // 1. สร้าง Transaction สำหรับการฝากเงิน (DEPOSIT)
            depositTransactionDto = TransactionDTO().apply {
                member = MemberDTO().apply { this.id = memberId }
                transactionType = "DEPOSIT"
                transactionAmount = amount
                transactionStatus = TransactionStatusEnum.PENDING
            }

            // 2. บันทึก Transaction เพื่อให้ได้ ID สำหรับใช้กับ Omise
            savedTransactionDto = transactionService.saveTransaction(depositTransactionDto)

            // 3. เรียก Omise Service เพื่อสร้าง QR Code
            val omiseQrResponse = omiseService.createPromptPayQRCode(
                amount,
                savedTransactionDto.transactionId.toString()
            )

            if (omiseQrResponse != null && omiseQrResponse.containsKey("qrCodeImageBase64")) {
                val svgBase64 = omiseQrResponse["qrCodeImageBase64"]

                // 4. อัปเดตสถานะ Transaction เมื่อ QR Code ถูกสร้างแล้ว
                savedTransactionDto.transactionStatus = TransactionStatusEnum.PENDING
                transactionService.saveTransaction(savedTransactionDto)

                ResponseEntity.ok().body(
                    HttpResponse(
                        true,
                        "สร้าง QR Code สำเร็จ",
                        mapOf(
                            "transactionId" to savedTransactionDto.transactionId!!,
                            "qrCodeImageBase64" to svgBase64!!
                        )
                    )
                )
            } else {
              savedTransactionDto.let {
                it.transactionStatus = TransactionStatusEnum.FAILED
                transactionService.saveTransaction(it)
              }
                ResponseEntity.badRequest().body(
                    HttpResponse(
                        false,
                        "Failed to generate QR Code from Omise API or no QR data returned."
                    )
                )
            }
        } catch (e: BadRequestException) {
            val failedDto = savedTransactionDto ?: depositTransactionDto
            if (failedDto?.transactionId != null) {
                try {
                    failedDto.transactionStatus = TransactionStatusEnum.FAILED
                    transactionService.saveTransaction(failedDto)
                } catch (ex: Exception) { /* ignore secondary save error */ }
            }
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "ส่งคำขอไม่ถูกต้อง"
                )
            )
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.badRequest().body(
                HttpResponse(
                    false,
                    e.message ?: "สร้าง QR Code ไม่สำเร็จ"
                )
            )
        }
    }
}

