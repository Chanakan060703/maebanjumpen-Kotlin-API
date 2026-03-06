package com.itsci.mju.maebanjumpen.transaction.constant

enum class TransactionTypeEnum (val value: Long){
  WITHDRAWAL(1),
  DEPOSIT(2);

  companion object {
        fun fromValue(value: Long): TransactionTypeEnum? {
            return TransactionTypeEnum.entries.firstOrNull { it.value == value }
        }
    }
}