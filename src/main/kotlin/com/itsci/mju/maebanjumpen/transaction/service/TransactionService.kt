package com.itsci.mju.maebanjumpen.transaction.service

import com.fasterxml.jackson.databind.JsonNode
import com.itsci.mju.maebanjumpen.transaction.dto.TransactionDTO
import java.util.Optional

interface TransactionService {
    fun getAllTransactions(): List<TransactionDTO>
    fun getTransactionById(id: Long): Optional<TransactionDTO>
    fun saveTransaction(transactionDto: TransactionDTO): TransactionDTO
    fun deleteTransaction(id: Long): Boolean
    fun getTransactionsByMemberId(memberId: Long): List<TransactionDTO>
    fun getWithdrawalRequests(): List<TransactionDTO>
    fun updateWithdrawalRequestStatus(transactionId: Long, newStatus: String): Optional<TransactionDTO>
    fun processOmiseChargeComplete(omisePayload: JsonNode)
    fun getLocalizedStatus(status: String?, isEnglish: Boolean): String
    fun getStatusColorHex(status: String?): String
}

