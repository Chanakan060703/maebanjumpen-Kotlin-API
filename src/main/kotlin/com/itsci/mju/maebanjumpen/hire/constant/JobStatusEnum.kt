package com.itsci.mju.maebanjumpen.hire.constant

enum class JobStatusEnum(val value: String) {
  PENDING("PENDING"),
  IN_PROGRESS("IN_PROGRESS"),
  COMPLETED("COMPLETED"),
  CANCELLED("CANCELLED"),
  REPORTED("REPORTED");

  companion object {
    fun fromValue(value: String?): JobStatusEnum? {
      return entries.find { it.value.equals(value, ignoreCase = true) }
    }
  }
}