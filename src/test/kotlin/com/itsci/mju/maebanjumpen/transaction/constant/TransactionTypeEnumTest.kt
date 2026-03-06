package com.itsci.mju.maebanjumpen.transaction.constant

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.itsci.mju.maebanjumpen.transaction.converter.TransactionTypeConverter
import com.itsci.mju.maebanjumpen.transaction.dto.TransactionDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TransactionTypeEnumTest {
    private val objectMapper = ObjectMapper().registerKotlinModule()
    private val converter = TransactionTypeConverter()

    @Test
    fun `deserializes transaction type from number and alias string`() {
        assertEquals(TransactionTypeEnum.DEPOSIT, objectMapper.readValue("2", TransactionTypeEnum::class.java))
        assertEquals(TransactionTypeEnum.WITHDRAWAL, objectMapper.readValue("\"WITHDRAW\"", TransactionTypeEnum::class.java))
    }

    @Test
    fun `transaction dto accepts numeric transaction type`() {
        val dto = objectMapper.readValue("""{"transactionType":1}""", TransactionDTO::class.java)

        assertEquals(TransactionTypeEnum.WITHDRAWAL, dto.transactionType)
    }

    @Test
    fun `converter maps enum values to integer column`() {
        assertEquals(1, converter.convertToDatabaseColumn(TransactionTypeEnum.WITHDRAWAL))
        assertEquals(TransactionTypeEnum.DEPOSIT, converter.convertToEntityAttribute(2))
        assertNull(converter.convertToDatabaseColumn(null))
    }
}