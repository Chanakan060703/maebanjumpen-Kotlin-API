package com.itsci.mju.maebanjumpen.entity

import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class PartyRoleMappingTest {

    @Test
    fun `PartyRole should map to party_role single table`() {
        val table = PartyRole::class.java.getAnnotation(Table::class.java)
        val inheritance = PartyRole::class.java.getAnnotation(Inheritance::class.java)

        assertNotNull(table)
        assertEquals("party_role", table.name)
        assertNotNull(inheritance)
        assertEquals(InheritanceType.SINGLE_TABLE, inheritance.strategy)
    }
}