package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.itsci.mju.maebanjumpen.transaction.constant.TransactionTypeEnum
import com.itsci.mju.maebanjumpen.transaction.converter.TransactionTypeConverter
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Convert(converter = TransactionTypeConverter::class)
    @Column(name = "type", nullable = false)
    var transactionType: TransactionTypeEnum = TransactionTypeEnum.DEPOSIT,

    @Column(name = "amount", nullable = false)
    var transactionAmount: Double = 0.0,

    @Column(name = "date", nullable = false)
    var transactionDate: LocalDateTime? = null,

    @Column(name = "status", nullable = false, length = 255)
    var transactionStatus: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_role_id", nullable = false)
    var partyRole: PartyRole? = null,

    @Column(name = "prompay_number", length = 50)
    var prompayNumber: String? = null,

    @Column(name = "bank_account_number", length = 50)
    var bankAccountNumber: String? = null,

    @Column(name = "bank_account_name", length = 255)
    var bankAccountName: String? = null,

    @Column(name = "transaction_approval_date")
    var transactionApprovalDate: LocalDateTime? = null,

    @Column(name = "create_at")
    var createAt: LocalDateTime? = null,

    @Column(name = "update_at")
    var updateAt: LocalDateTime? = null,

    @Column(name = "is_delete")
    var isDelete: Boolean? = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Transaction) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Transaction(transactionId=$id, transactionType=$transactionType, transactionAmount=$transactionAmount)"
}

