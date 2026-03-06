package com.itsci.mju.maebanjumpen.report.constant

enum class ReportStatusEnum(val value: String) {
  SUBMITTED("SUBMITTED"),
  IN_PROGRESS("IN_PROGRESS"),
  RESOLVED("RESOLVED"),
  CLOSED("CLOSED");

  companion object {
    fun fromValue(value: String?): ReportStatusEnum? {
      return ReportStatusEnum.entries.find { it.value == value }
    }
  }
}