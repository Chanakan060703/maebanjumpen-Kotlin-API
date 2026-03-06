package com.itsci.mju.maebanjumpen.transaction.constant

enum class TransactionStatusEnum(val value: String) {
  APPROVED("APPROVED"),
  REJECTED("REJECTED"),
  PENDING("PENDING"),
  COMPLETED("COMPLETED"),
  FAILED("FAILED"),
  SUCCESS("SUCCESS");

  companion object {
    fun fromValue(value: String?): TransactionStatusEnum? {
      return TransactionStatusEnum.entries.find { it.value == value }
    }
  }
}