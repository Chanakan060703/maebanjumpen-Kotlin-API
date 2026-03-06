package com.itsci.mju.maebanjumpen.transaction.service.impl

import com.fasterxml.jackson.databind.JsonNode
import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.entity.Transaction
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.MemberRepository
import com.itsci.mju.maebanjumpen.transaction.constant.TransactionStatusEnum
import com.itsci.mju.maebanjumpen.transaction.constant.TransactionTypeEnum
import com.itsci.mju.maebanjumpen.transaction.dto.TransactionDTO
import com.itsci.mju.maebanjumpen.transaction.repository.TransactionRepository
import com.itsci.mju.maebanjumpen.transaction.service.TransactionService
import org.hibernate.Hibernate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import java.util.Optional

@Service
class TransactionServiceImpl @Autowired internal constructor(
    private val transactionRepository: TransactionRepository,
    private val memberRepository: MemberRepository
) : TransactionService {

    private fun mapTransactionToDto(transaction: Transaction): TransactionDTO {
        return TransactionDTO(
            transactionType = transaction.transactionType,
            transactionAmount = transaction.transactionAmount,
            transactionDate = transaction.transactionDate,
            transactionStatus = TransactionStatusEnum.fromValue(transaction.transactionStatus),
            member = (transaction.partyRole as? com.itsci.mju.maebanjumpen.entity.Member)?.let { member ->
                MemberDTO().apply {
                    id = member.id
                    balance = member.balance
                }
            },
            prompayNumber = transaction.prompayNumber,
            bankAccountNumber = transaction.bankAccountNumber,
            bankAccountName = transaction.bankAccountName,
            transactionApprovalDate = transaction.transactionApprovalDate
        ).apply {
            transactionId = transaction.id
        }
    }

    private fun mapDtoToTransaction(dto: TransactionDTO): Transaction {
        return Transaction(
            id = dto.transactionId,
            transactionType = dto.transactionType ?: throw IllegalArgumentException("Transaction type is required."),
            transactionAmount = dto.transactionAmount ?: 0.0,
            transactionDate = dto.transactionDate,
            transactionStatus = dto.transactionStatus?.value ?: "",
            prompayNumber = dto.prompayNumber,
            bankAccountNumber = dto.bankAccountNumber,
            bankAccountName = dto.bankAccountName,
            transactionApprovalDate = dto.transactionApprovalDate
        )
    }

    private fun initializeTransactionMemberAndRelated(transaction: Transaction?) {
        if (transaction?.partyRole != null) {
            Hibernate.initialize(transaction.partyRole)
            val partyRole = transaction.partyRole!!

            partyRole.person?.let { person ->
                Hibernate.initialize(person)
            }
        }
    }

    @Transactional(readOnly = true)
    override fun getAllTransactions(): List<TransactionDTO> {
        val transactions = transactionRepository.findAll()
        transactions.forEach { initializeTransactionMemberAndRelated(it) }
        return transactions.map { mapTransactionToDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getTransactionById(id: Long): Optional<TransactionDTO> {
        val transactionOptional = transactionRepository.findById(id)
        transactionOptional.ifPresent { initializeTransactionMemberAndRelated(it) }
        return transactionOptional.map { mapTransactionToDto(it) }
    }

    @Transactional
    override fun saveTransaction(transactionDto: TransactionDTO): TransactionDTO {
        if (transactionDto.member?.id == null) {
            throw IllegalArgumentException("Member ID is required for transaction. Please provide member object with ID.")
        }

        val memberId = transactionDto.member!!.id!!

        val existingMember = memberRepository.findById(memberId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with ID: $memberId") }

        val transaction = mapDtoToTransaction(transactionDto)
        transaction.partyRole = existingMember

        if (transaction.transactionDate == null) {
            transaction.transactionDate = LocalDateTime.now()
        }

        if (transaction.transactionType == TransactionTypeEnum.WITHDRAWAL) {
            val hasPrompay = !transaction.prompayNumber.isNullOrBlank()
            val hasBankDetails = !transaction.bankAccountNumber.isNullOrBlank() && !transaction.bankAccountName.isNullOrBlank()

            if (!hasPrompay && !hasBankDetails) {
                throw IllegalArgumentException("Withdrawal transaction requires either Prompay number or complete Bank Account details (number and name).")
            }
        }

        var oldStatus: String? = null
        transaction.id?.let { id ->
            transactionRepository.findById(id).ifPresent { oldStatus = it.transactionStatus }
        }

        if (transaction.transactionStatus.isEmpty()) {
            transaction.transactionStatus = TransactionStatusEnum.PENDING.value
        }

        val currentTransactionStatus = TransactionStatusEnum.fromValue(transaction.transactionStatus)
        if (currentTransactionStatus in listOf(TransactionStatusEnum.APPROVED, TransactionStatusEnum.REJECTED, TransactionStatusEnum.COMPLETED, TransactionStatusEnum.SUCCESS)) {
            if (transaction.transactionApprovalDate == null) {
                transaction.transactionApprovalDate = LocalDateTime.now()
            }
        } else {
            transaction.transactionApprovalDate = null
        }

        val savedTransaction = transactionRepository.save(transaction)
        val savedStatus = TransactionStatusEnum.fromValue(savedTransaction.transactionStatus)
        val oldStatusEnum = TransactionStatusEnum.fromValue(oldStatus)

        if ((savedStatus == TransactionStatusEnum.APPROVED || savedStatus == TransactionStatusEnum.SUCCESS) &&
            !(oldStatusEnum == TransactionStatusEnum.APPROVED || oldStatusEnum == TransactionStatusEnum.SUCCESS)) {

            val memberToUpdate = savedTransaction.partyRole as? com.itsci.mju.maebanjumpen.entity.Member
            if (memberToUpdate != null) {
                val currentBalance = memberToUpdate.balance ?: 0.0
                val transactionAmount = savedTransaction.transactionAmount

                if (savedTransaction.transactionType == TransactionTypeEnum.WITHDRAWAL) {
                    if (currentBalance >= transactionAmount) {
                        memberToUpdate.balance = currentBalance - transactionAmount
                        memberRepository.save(memberToUpdate)
                    } else {
                        savedTransaction.transactionStatus = TransactionStatusEnum.FAILED.value
                        savedTransaction.transactionApprovalDate = LocalDateTime.now()
                        transactionRepository.save(savedTransaction)
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds for withdrawal ($currentBalance < $transactionAmount)")
                    }
                } else if (savedTransaction.transactionType == TransactionTypeEnum.DEPOSIT) {
                    memberToUpdate.balance = currentBalance + transactionAmount
                    memberRepository.save(memberToUpdate)
                }
            }
        }

        initializeTransactionMemberAndRelated(savedTransaction)
        return mapTransactionToDto(savedTransaction)
    }

    @Transactional
    override fun deleteTransaction(id: Long): Boolean {
        val transaction = transactionRepository.findById(id)
            .orElseThrow { NotFoundException("Transaction not found with id: $id") }

        if (transaction.isDelete == true) {
            throw BadRequestException("ธุรกรรมนี้ถูกลบแล้ว")
        }

        transaction.isDelete = true
        transactionRepository.save(transaction)
        return true
    }

    @Transactional(readOnly = true)
    override fun getTransactionsByMemberId(memberId: Long): List<TransactionDTO> {
        val transactions = transactionRepository.findByPartyRoleId(memberId)
        transactions.forEach { initializeTransactionMemberAndRelated(it) }
        return transactions.map { mapTransactionToDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getWithdrawalRequests(): List<TransactionDTO> {
        val withdrawalTransactions = transactionRepository.findByTransactionType(TransactionTypeEnum.WITHDRAWAL)
        withdrawalTransactions.forEach { initializeTransactionMemberAndRelated(it) }

        val sortedTransactions = withdrawalTransactions.sortedWith(compareBy<Transaction> { transaction ->
            val status = transaction.transactionStatus.lowercase()
            val isPending = status == "pending approve" || status == "กำลังรอตรวจสอบ"
            if (isPending) 0 else 1
        }.thenByDescending { it.transactionDate ?: LocalDateTime.MIN })

        return sortedTransactions.map { mapTransactionToDto(it) }
    }

    @Transactional
    override fun updateWithdrawalRequestStatus(transactionId: Long, newStatus: String): Optional<TransactionDTO> {
        return transactionRepository.findById(transactionId).map { existingTransaction ->
            val oldStatusEnum = TransactionStatusEnum.fromValue(existingTransaction.transactionStatus)

            existingTransaction.transactionStatus = newStatus

            val newStatusEnum = TransactionStatusEnum.fromValue(newStatus)
            if (newStatusEnum in listOf(TransactionStatusEnum.APPROVED, TransactionStatusEnum.REJECTED, TransactionStatusEnum.COMPLETED, TransactionStatusEnum.SUCCESS)) {
                existingTransaction.transactionApprovalDate = LocalDateTime.now()
            } else {
                existingTransaction.transactionApprovalDate = null
            }

            val savedTransaction = transactionRepository.save(existingTransaction)
            val savedStatusEnum = TransactionStatusEnum.fromValue(savedTransaction.transactionStatus)

            if ((savedStatusEnum == TransactionStatusEnum.APPROVED || savedStatusEnum == TransactionStatusEnum.SUCCESS) &&
                !(oldStatusEnum == TransactionStatusEnum.APPROVED || oldStatusEnum == TransactionStatusEnum.SUCCESS)) {

                val memberToUpdate = savedTransaction.partyRole as? com.itsci.mju.maebanjumpen.entity.Member
                if (memberToUpdate != null) {
                    val currentBalance = memberToUpdate.balance ?: 0.0
                    val transactionAmount = savedTransaction.transactionAmount

                    if (savedTransaction.transactionType == TransactionTypeEnum.WITHDRAWAL) {
                        if (currentBalance >= transactionAmount) {
                            memberToUpdate.balance = currentBalance - transactionAmount
                            memberRepository.save(memberToUpdate)
                        } else {
                            savedTransaction.transactionStatus = TransactionStatusEnum.FAILED.value
                            savedTransaction.transactionApprovalDate = LocalDateTime.now()
                            transactionRepository.save(savedTransaction)
                            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds for withdrawal ($currentBalance < $transactionAmount)")
                        }
                    } else if (savedTransaction.transactionType == TransactionTypeEnum.DEPOSIT) {
                        memberToUpdate.balance = currentBalance + transactionAmount
                        memberRepository.save(memberToUpdate)
                    }
                }
            }
            initializeTransactionMemberAndRelated(savedTransaction)
            mapTransactionToDto(savedTransaction)
        }
    }

    @Transactional
    override fun processOmiseChargeComplete(root: JsonNode) {
        val chargeStatus = root.path("data").path("status").asText()
        val paid = root.path("data").path("paid").asBoolean()
        val amountInSatang = root.path("data").path("amount").asDouble()
        val ourTransactionId = root.path("data").path("metadata").path("transaction_id").asText()

        if (ourTransactionId.isNullOrEmpty()) {
            throw IllegalArgumentException("Metadata 'transaction_id' is missing from Omise payload.")
        }

        val optionalTransaction = transactionRepository.findById(ourTransactionId.toLong())

        if (optionalTransaction.isPresent) {
            val transaction = optionalTransaction.get()
            val oldStatus = transaction.transactionStatus

            if (transaction.partyRole?.id == null) {
                System.err.println("Error: PartyRole not found or invalid ID for transaction ID: $ourTransactionId")
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "PartyRole associated with transaction not found.")
            }

            val oldStatusEnum = TransactionStatusEnum.fromValue(oldStatus)

            when {
                chargeStatus == "successful" && paid -> {
                    transaction.transactionStatus = TransactionStatusEnum.SUCCESS.value
                    transaction.transactionApprovalDate = LocalDateTime.now()

                    val member = transaction.partyRole as? com.itsci.mju.maebanjumpen.entity.Member
                    if (member != null) {
                        if (oldStatusEnum !in listOf(TransactionStatusEnum.SUCCESS, TransactionStatusEnum.APPROVED)) {
                            val amountInBaht = amountInSatang / 100.0
                            val currentMemberBalance = member.balance ?: 0.0
                            member.balance = currentMemberBalance + amountInBaht
                            memberRepository.save(member)
                            println("Member ID: ${member.id} balance updated to: ${member.balance}")
                        } else {
                            println("Transaction ID: $ourTransactionId already processed/approved. Skipping balance update.")
                        }
                    }
                }
                chargeStatus == "failed" -> {
                    transaction.transactionStatus = TransactionStatusEnum.FAILED.value
                    println("Transaction ID: $ourTransactionId failed.")
                }
                else -> {
                    transaction.transactionStatus = TransactionStatusEnum.fromValue(chargeStatus)?.value ?: chargeStatus.uppercase()
                    println("Transaction ID: $ourTransactionId status: $chargeStatus")
                }
            }

            transactionRepository.save(transaction)
        } else {
            System.err.println("Transaction with ID $ourTransactionId not found in our system.")
        }
    }

    override fun getLocalizedStatus(status: String?, isEnglish: Boolean): String {
        if (status == null) {
            return if (isEnglish) "Unknown" else "ไม่ทราบสถานะ"
        }
        return if (isEnglish) {
            status
        } else {
            when (status.uppercase()) {
                "PENDING APPROVE", "PENDING" -> "กำลังรอตรวจสอบ"
                "PENDING PAYMENT", "QR GENERATED" -> "รอชำระเงิน"
                "APPROVED" -> "อนุมัติแล้ว"
                "REJECTED" -> "ถูกปฏิเสธ"
                "COMPLETED" -> "เสร็จสิ้น"
                "FAILED" -> "ล้มเหลว"
                "SUCCESS" -> "สำเร็จ"
                else -> "ไม่ทราบสถานะ"
            }
        }
    }

    override fun getStatusColorHex(status: String?): String {
        if (status == null) return "#808080"

        return when (status.uppercase()) {
            "PENDING APPROVE", "PENDING PAYMENT", "QR GENERATED" -> "#FFA500"
            "APPROVED", "COMPLETED", "SUCCESS" -> "#008000"
            "REJECTED", "FAILED" -> "#FF0000"
            else -> "#808080"
        }
    }
}

