package com.itsci.mju.maebanjumpen.transaction.constant

import com.fasterxml.jackson.annotation.JsonCreator

enum class TransactionTypeEnum(val value: Int) {
    WITHDRAWAL(1),
    DEPOSIT(2);

    companion object {
        private val byValue = entries.associateBy { it.value }

        fun fromValue(value: Int?): TransactionTypeEnum? = value?.let(byValue::get)

        fun fromValue(value: Long?): TransactionTypeEnum? = value?.toInt()?.let(byValue::get)

        fun fromName(value: String?): TransactionTypeEnum? {
            val normalized = value?.trim()?.uppercase() ?: return null
            return when (normalized) {
                "WITHDRAW" -> WITHDRAWAL
                else -> entries.firstOrNull { it.name == normalized }
            }
        }

        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun fromJson(value: Any?): TransactionTypeEnum? = when (value) {
            null -> null
            is Number -> fromValue(value.toInt())
            is String -> fromName(value) ?: value.toIntOrNull()?.let(::fromValue)
            else -> null
        }
    }
}