package com.itsci.mju.maebanjumpen.hire.request

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CreateHireRequest {
  var hireName: String = ""
  var hireDetail: String = ""
  var paymentAmount: Double = 0.0
  var startDate: LocalDate? = null
  var startTime: LocalTime? = null
  var endTime: LocalTime? = null
  var location: String = ""
  var hirerId: Long? = null
  var housekeeperId: Long? = null
  var skillTypeId: Long? = null
  var jobStatus: String? = null
  var hireDate: LocalDateTime? = null
  var progressionImageUrls: List<String>? = null
}