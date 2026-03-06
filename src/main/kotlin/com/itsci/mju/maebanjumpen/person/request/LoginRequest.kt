package com.itsci.mju.maebanjumpen.person.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
  @field:NotBlank(message = "Username is required")
  val username: String,
  @field:Size(min = 5, message = "Password must be at least 5 characters")
  val password: String
)