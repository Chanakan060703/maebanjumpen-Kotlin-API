package com.itsci.mju.maebanjumpen.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.itsci.mju.maebanjumpen.person.dto.PersonJwt
import com.itsci.mju.maebanjumpen.person.dto.PersonPrincipal
import com.itsci.mju.maebanjumpen.token.JWTTokenProvider
import com.itsci.mju.maebanjumpen.token.dto.TokenDto
import com.itsci.mju.maebanjumpen.token.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
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
                val personJwt = objectMapper.readValue(payload, PersonJwt::class.java)

                if (tokenService.isValidToken(TokenDto(userId = personJwt.uid.toString(), token = jwt))) {
                    val personPrincipal = PersonPrincipal().apply {
                        setPersonId(personJwt.uid ?: 0)
                        setEmail(personJwt.email)
                        setUsername(personJwt.username)
                        setFirstName(personJwt.firstName)
                        setLastName(personJwt.lastName)
                        setRole(personJwt.role)
                        setPartyRoleId(personJwt.partyRoleId)
                    }

                    val authorities: List<GrantedAuthority> = personJwt.role?.let {
                        listOf(SimpleGrantedAuthority(it))
                    } ?: emptyList()

                    val authentication = UsernamePasswordAuthenticationToken(
                        personPrincipal,
                        null,
                        authorities
                    )
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    logger.warn("Token is not valid in cache")
                }
            }
        } catch (ex: Exception) {
            logger.error("Cannot set user authentication: ${ex.message}")
        }
        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }

        val tokenJwt = request.getHeader("x-api-key")
        return if (StringUtils.hasText(tokenJwt)) {
            tokenJwt
        } else {
            null
        }
    }
}