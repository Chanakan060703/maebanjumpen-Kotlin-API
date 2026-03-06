package com.itsci.mju.maebanjumpen.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.lucablock.backofficeapi.token.JWTTokenProvider
import com.lucablock.backofficeapi.token.dto.TokenDto
import com.lucablock.backofficeapi.token.service.TokenService
import com.lucablock.backofficeapi.user.dto.UserJwt
import com.lucablock.backofficeapi.user.dto.UserPrincipal
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JWTAuthorizationFilter : OncePerRequestFilter() {

  @Autowired
  lateinit var tokenProvider: JWTTokenProvider

  @Autowired
  lateinit var objectMapper: ObjectMapper

  @Autowired
  lateinit var tokenService: TokenService

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    try {
      val jwt = getJwtFromRequest(request)
      if (!jwt.isNullOrBlank() && tokenProvider.validateToken(jwt)) {
        val claims = tokenProvider.getClaims(jwt)
        val payload = claims["user"].toString()
        val userJwt = objectMapper.readValue(payload, UserJwt::class.java)
        if (tokenService.isValidToken(TokenDto(userId = userJwt.uid.toString(), token = jwt))) {
          val userDetails = UserPrincipal()
          userDetails.setUserId(userJwt.uid)
          userDetails.setEmail(userJwt.email)
          userDetails.setUsername(userJwt.username)
          userDetails.setCompanyId(userJwt.cid)
          userDetails.setUserType(userJwt.userType)
          userDetails.setUserTypeId(userJwt.userTypeId)
          userDetails.setPlatform(userJwt.pid)
//                        val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority(userDetails.getUserType()))
          val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority(userJwt.userType))
          val authentication = UsernamePasswordAuthenticationToken(userDetails, null, authorities)
          SecurityContextHolder.getContext().authentication = authentication
        } else {
          throw AccessDeniedException("Token หมดอายุ")
        }
      }
    } catch (ex: Exception) {
      logger.error(ex.message)
    }
    filterChain.doFilter(request, response)
  }

//  private fun getJwtFromRequest(request: HttpServletRequest): String? {
//    val bearerToken = request.getHeader("Authorization")
//    return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//      bearerToken.substring(7, bearerToken.length)
//    } else null
//  }
//
//  private fun getTokenJwtFromRequest(request: HttpServletRequest): String? {
//    val tokenJwt = request.getHeader("x-api-key")
//    return tokenJwt
//  }

  private fun getJwtFromRequest(request: HttpServletRequest): String? {
    val bearerToken = request.getHeader("Authorization")
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7, bearerToken.length)
    }

    val tokenJwt = request.getHeader("x-api-key")
    return if (StringUtils.hasText(tokenJwt)) {
      tokenJwt
    } else {
      null
    }
  }

}