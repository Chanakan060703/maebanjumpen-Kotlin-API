package com.itsci.mju.maebanjumpen.hire.request

import jakarta.validation.constraints.NotBlank

class UpdateHireRequest {
  @field:NotBlank(message = "Id is required")
  var id: Long = 0
  @field:NotBlank(message = "Hire name is required")
  var hireName: String? = null
  @field:NotBlank(message = "Hire detail is required")
  var hireDetail: String? = null
  @field:NotBlank(message = "Payment amount is required")
  var paymentAmount: Double? = null
  @field:NotBlank(message = "Hire date is required")
  var startDate: String? = null
  @field:NotBlank(message = "Start time is required")
  var startTime: String? = null
  @field:NotBlank(message = "End time is required")
  var endTime: String? = null
  @field:NotBlank(message = "Location is required")
  var location: String? = null
  @field:NotBlank(message = "Job status is required")
  var jobStatus: String? = null
}