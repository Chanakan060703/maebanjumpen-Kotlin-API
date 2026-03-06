package com.itsci.mju.maebanjumpen.penalty.constant

enum class PenaltyStatusEnum(val value: String) {
  SUSPENDED("SUSPENDED"),
  WARNING("WARNING"),
  ACTIVE("ACTIVE"),
  INACTIVE("INACTIVE");

  companion object {
    fun fromValue(value: String?): PenaltyStatusEnum? {
      return entries.find { it.value.equals(value, ignoreCase = true) }
    }
  }
}