package com.itsci.mju.maebanjumpen.token

import com.fasterxml.jackson.databind.ObjectMapper
import com.itsci.mju.maebanjumpen.person.dto.PersonJwt
import com.itsci.mju.maebanjumpen.person.dto.PersonPrincipal
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JWTTokenProvider {
    private val logger = LoggerFactory.getLogger(JWTTokenProvider::class.java)

    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.expiration-ms}")
    private var jwtExpirationInMs: Long = 86400000

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    fun generateToken(personPrincipal: PersonPrincipal): String {
        val personJwt = PersonJwt(
            uid = personPrincipal.getPersonId(),
            email = personPrincipal.getEmail(),
            username = personPrincipal.username,
            firstName = personPrincipal.getFirstName(),
            lastName = personPrincipal.getLastName(),
            role = personPrincipal.getRole(),
            partyRoleId = personPrincipal.getPartyRoleId()
        )

        val payload = objectMapper.writeValueAsString(personJwt)

        return Jwts.builder()
            .setIssuer("MAEBANJUMPEN")
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationInMs))
            .setSubject(personPrincipal.getEmail())
            .setId(personPrincipal.getPersonId().toString())
            .claim("user", payload)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun createToken(authentication: Authentication): String {
        val personPrincipal = authentication.principal as PersonPrincipal
        return generateToken(personPrincipal)
    }

    fun validateToken(jwt: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature: ${ex.message}")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token: ${ex.message}")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token: ${ex.message}")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token: ${ex.message}")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty: ${ex.message}")
        }
        return false
    }

    fun getUserNameFromToken(token: String): String {
        return getClaims(token).subject
    }

    fun getUserId(token: String): Long {
        val cleanToken = token.replace("Bearer ", "")
        return getClaims(cleanToken).id.toLong()
    }

    fun getClaims(token: String): Claims {
        val cleanToken = token.replace("Bearer ", "")
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(cleanToken)
            .body
    }

    fun getPersonJwtFromToken(token: String): PersonJwt {
        val claims = getClaims(token)
        val payload = claims["user"]?.toString()
            ?: throw IllegalArgumentException("User claim is missing or invalid")
        return objectMapper.readValue(payload, PersonJwt::class.java)
    }
}