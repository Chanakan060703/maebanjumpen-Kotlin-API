package com.itsci.mju.maebanjumpen.transaction.repository

import com.itsci.mju.maebanjumpen.entity.Transaction
import com.itsci.mju.maebanjumpen.transaction.constant.TransactionTypeEnum
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {

    fun findByTransactionTypeAndTransactionStatus(transactionType: TransactionTypeEnum, transactionStatus: String): List<Transaction>

    fun findByPartyRoleId(partyRoleId: Long): List<Transaction>

    @EntityGraph(attributePaths = ["partyRole", "partyRole.person"])
    override fun findById(id: Long): Optional<Transaction>

    fun findByTransactionType(transactionType: TransactionTypeEnum): List<Transaction>
}

