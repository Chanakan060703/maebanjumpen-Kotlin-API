package com.itsci.mju.maebanjumpen.partyrole.constant

enum class StatusVerify(val value: String) {
  PENDING("PENDING"),
  APPROVED("APPROVED"),
  REJECTED("REJECTED"),
  VERIFIED("VERIFIED"),
  NOT_VERIFIED("NOT_VERIFIED");

  companion object {
    fun fromValue(value: String): StatusVerify? {
      return StatusVerify.entries.find { it.value == value }
    }
  }
}