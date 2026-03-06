package com.itsci.mju.maebanjumpen.person.controller

import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.person.dto.PersonPrincipal
import com.itsci.mju.maebanjumpen.person.request.LoginRequest
import com.itsci.mju.maebanjumpen.person.service.AuthService
import com.itsci.mju.maebanjumpen.token.JWTTokenProvider
import com.itsci.mju.maebanjumpen.token.dto.TokenDto
import com.itsci.mju.maebanjumpen.token.service.TokenService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Legacy PersonController - provides backward compatibility for /api/user endpoints
 * New implementations should use AuthController at /api/auth
 */
@RestController
@RequestMapping("/maeban/user")
class PersonController @Autowired internal constructor(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JWTTokenProvider,
    private val tokenService: TokenService,
    private val authService: AuthService
) {
    private val logger = LoggerFactory.getLogger(PersonController::class.java)

    /**
     * Legacy login endpoint - redirects to new auth system
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<HttpResponse> {
        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
                )
            )

            if (authentication.isAuthenticated) {
                val personPrincipal = authentication.principal as PersonPrincipal
                val token = jwtTokenProvider.generateToken(personPrincipal)

                tokenService.cacheToken(
                    TokenDto(
                        userId = personPrincipal.getPersonId().toString(),
                        token = token,
                        multipleLogin = true
                    )
                )

                return ResponseEntity.ok(
                    HttpResponse(
                        status = true,
                        message = "เข้าสู่ระบบสำเร็จ",
                        data = mapOf("token" to token)
                    )
                )
            }

            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                HttpResponse(
                    status = false,
                    message = "ไม่สามารถเข้าสู่ระบบได้ ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง"
                )
            )
        } catch (e: BadCredentialsException) {
            logger.error("Login error: ${e.message}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                HttpResponse(
                    status = false,
                    message = "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง"
                )
            )
        } catch (e: Exception) {
            logger.error("Login error: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpResponse(
                    status = false,
                    message = "เกิดข้อผิดพลาดในการเข้าสู่ระบบ"
                )
            )
        }
    }

    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal personPrincipal: PersonPrincipal): ResponseEntity<HttpResponse> {
        return try {
            tokenService.revokeToken(personPrincipal.getPersonId())
            ResponseEntity.ok(
                HttpResponse(
                    status = true,
                    message = "ออกจากระบบสำเร็จ"
                )
            )
        } catch (e: Exception) {
            logger.error("Logout error: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpResponse(
                    status = false,
                    message = "เกิดข้อผิดพลาดในการออกจากระบบ"
                )
            )
        }
    }

    @GetMapping("/me")
    fun getUser(@AuthenticationPrincipal personPrincipal: PersonPrincipal): ResponseEntity<HttpResponse> {
        return try {
            val fullPrincipal = authService.getPersonPrincipal(personPrincipal.getPersonId())
            ResponseEntity.ok(
                HttpResponse(
                    status = true,
                    message = "ดึงข้อมูลผู้ใช้สำเร็จ",
                    data = mapOf(
                        "id" to fullPrincipal.getPersonId(),
                        "username" to fullPrincipal.username,
                        "email" to fullPrincipal.getEmail(),
                        "firstName" to fullPrincipal.getFirstName(),
                        "lastName" to fullPrincipal.getLastName(),
                        "role" to fullPrincipal.getRole(),
                        "partyRoleId" to fullPrincipal.getPartyRoleId()
                    )
                )
            )
        } catch (e: Exception) {
            logger.error("Get user error: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpResponse(
                    status = false,
                    message = "เกิดข้อผิดพลาดในการดึงข้อมูลผู้ใช้"
                )
            )
        }
    }
}