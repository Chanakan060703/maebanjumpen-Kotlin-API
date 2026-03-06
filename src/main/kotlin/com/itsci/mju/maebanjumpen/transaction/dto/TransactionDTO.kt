package com.itsci.mju.maebanjumpen.transaction.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import com.itsci.mju.maebanjumpen.transaction.constant.TransactionTypeEnum
import com.itsci.mju.maebanjumpen.transaction.constant.TransactionStatusEnum
import java.time.LocalDateTime

data class TransactionDTO(
    var transactionType: TransactionTypeEnum? = null,
    var transactionAmount: Double? = null,
    var transactionDate: LocalDateTime? = null,
    var transactionStatus: TransactionStatusEnum? = null,
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    var member: MemberDTO? = null,
    var prompayNumber: String? = null,
    var bankAccountNumber: String? = null,
    var bankAccountName: String? = null,
    var transactionApprovalDate: LocalDateTime? = null
) {
    var transactionId: Long? = null
}

