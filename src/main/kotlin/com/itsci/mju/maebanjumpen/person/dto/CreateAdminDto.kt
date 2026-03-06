package com.itsci.mju.maebanjumpen.person.dto

data class CreateAdminDto (
  var username: String,
  var password: String,
  var email: String?,
  var firstName: String?,
  var lastName: String?,
  var idCardNumber: String?,
  var phoneNumber: String?,
  var address: String?
)