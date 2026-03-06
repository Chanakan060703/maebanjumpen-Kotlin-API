package com.itsci.mju.maebanjumpen.common.response
import java.sql.Timestamp

data class AuthResponse(
  val token: String? = null,
  val timestamp: Timestamp = Timestamp(System.currentTimeMillis()),
)