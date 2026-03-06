package com.itsci.mju.maebanjumpen.token.service.impl

import com.itsci.mju.maebanjumpen.token.dto.TokenDto
import com.itsci.mju.maebanjumpen.token.repository.TokenRepository
import com.itsci.mju.maebanjumpen.token.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TokenServiceImpl @Autowired internal constructor(
  private val tokenRepository: TokenRepository,
) : TokenService {

  override fun cacheToken(tokenDto: TokenDto) {
    tokenRepository.save(tokenDto)
  }

  override fun isValidToken(tokenDto: TokenDto): Boolean {
    val tokenRes = tokenRepository.findById(tokenDto.userId)
    if (tokenRes.isPresent) {
      return tokenRes.get().multipleLogin || tokenRes.get().token == tokenDto.token
    }
    return false
  }

  override fun revokeToken(userId: Long) {
    tokenRepository.deleteById(userId.toString())
  }
}