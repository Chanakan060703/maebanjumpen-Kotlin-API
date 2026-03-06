package com.itsci.mju.maebanjumpen.token.service
import com.itsci.mju.maebanjumpen.token.dto.TokenDto

interface TokenService {
  fun cacheToken(tokenDto: TokenDto)
  fun isValidToken(tokenDto: TokenDto): Boolean
  fun revokeToken(userId: Long)
}