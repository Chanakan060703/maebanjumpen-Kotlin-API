package com.itsci.mju.maebanjumpen.transaction.service.impl

import com.itsci.mju.maebanjumpen.entity.Member
import com.itsci.mju.maebanjumpen.entity.Transaction
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.MemberRepository
import com.itsci.mju.maebanjumpen.transaction.constant.TransactionTypeEnum
import com.itsci.mju.maebanjumpen.transaction.dto.TransactionDTO
import com.itsci.mju.maebanjumpen.transaction.repository.TransactionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import java.util.Optional

class TransactionServiceImplTest {
    private val transactionRepository = mock(TransactionRepository::class.java)
    private val memberRepository = mock(MemberRepository::class.java)
    private val service = TransactionServiceImpl(transactionRepository, memberRepository)

    @Test
    fun `saveTransaction rejects withdrawal without payout details`() {
        val member = Member(balance = 500.0).apply { id = 1L }
        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))

        val dto = TransactionDTO(
            transactionType = TransactionTypeEnum.WITHDRAWAL,
            transactionAmount = 100.0,
            member = MemberDTO().apply { id = 1L }
        )

        assertThrows(IllegalArgumentException::class.java) {
            service.saveTransaction(dto)
        }
    }

    @Test
    fun `getWithdrawalRequests returns sorted withdrawal transactions`() {
        val member = Member(balance = 500.0).apply { id = 1L }
        val success = Transaction(
            id = 2L,
            transactionType = TransactionTypeEnum.WITHDRAWAL,
            transactionAmount = 100.0,
            transactionDate = LocalDateTime.parse("2024-01-10T10:00:00"),
            transactionStatus = "SUCCESS",
            partyRole = member
        )
        val pending = Transaction(
            id = 1L,
            transactionType = TransactionTypeEnum.WITHDRAWAL,
            transactionAmount = 50.0,
            transactionDate = LocalDateTime.parse("2024-01-09T10:00:00"),
            transactionStatus = "PENDING APPROVE",
            partyRole = member
        )

        `when`(transactionRepository.findByTransactionType(TransactionTypeEnum.WITHDRAWAL))
            .thenReturn(listOf(success, pending))

        val result = service.getWithdrawalRequests()

        assertEquals(listOf(1L, 2L), result.map { it.transactionId })
        assertEquals(listOf(TransactionTypeEnum.WITHDRAWAL, TransactionTypeEnum.WITHDRAWAL), result.map { it.transactionType })
        verify(transactionRepository).findByTransactionType(TransactionTypeEnum.WITHDRAWAL)
    }
}