package com.itsci.mju.maebanjumpen.token.dto

import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "token", timeToLive = 864000)
data class TokenDto(
  @Id
  val userId: String,
  @NotNull
  val token: String,
  val multipleLogin: Boolean = false
)