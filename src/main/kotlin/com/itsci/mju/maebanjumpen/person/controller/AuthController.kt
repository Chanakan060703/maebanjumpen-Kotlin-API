package com.itsci.mju.maebanjumpen.person.controller

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import com.itsci.mju.maebanjumpen.person.dto.PersonPrincipal
import com.itsci.mju.maebanjumpen.person.request.LoginRequest
import com.itsci.mju.maebanjumpen.person.request.RegisterHirerRequest
import com.itsci.mju.maebanjumpen.person.request.RegisterHousekeeperRequest
import com.itsci.mju.maebanjumpen.person.service.AuthService
import com.itsci.mju.maebanjumpen.token.JWTTokenProvider
import com.itsci.mju.maebanjumpen.token.dto.TokenDto
import com.itsci.mju.maebanjumpen.token.service.TokenService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JWTTokenProvider,
    private val tokenService: TokenService
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/register/housekeeper")
    fun registerHousekeeper(
        @Valid @RequestBody request: RegisterHousekeeperRequest
    ): ResponseEntity<HttpResponse> {
        return try {
            val personPrincipal = authService.registerHousekeeper(request)
            val token = jwtTokenProvider.generateToken(personPrincipal)

            // Cache token
            tokenService.cacheToken(
                TokenDto(
                    userId = personPrincipal.getPersonId().toString(),
                    token = token,
                    multipleLogin = true
                )
            )

            ResponseEntity.status(HttpStatus.CREATED).body(
                HttpResponse(
                    status = true,
                    message = "ลงทะเบียนแม่บ้านสำเร็จ",
                    data = mapOf("token" to token)
                )
            )
        } catch (e: BadRequestException) {
            logger.error("Register housekeeper error: ${e.message}")
            ResponseEntity.badRequest().body(
                HttpResponse(status = false, message = e.message ?: "ลงทะเบียนไม่สำเร็จ")
            )
        } catch (e: Exception) {
            logger.error("Register housekeeper error: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpResponse(status = false, message = "เกิดข้อผิดพลาดในการลงทะเบียน")
            )
        }
    }

    @PostMapping("/register/hirer")
    fun registerHirer(
        @Valid @RequestBody request: RegisterHirerRequest
    ): ResponseEntity<HttpResponse> {
        return try {
            val personPrincipal = authService.registerHirer(request)
            val token = jwtTokenProvider.generateToken(personPrincipal)

            // Cache token
            tokenService.cacheToken(
                TokenDto(
                    userId = personPrincipal.getPersonId().toString(),
                    token = token,
                    multipleLogin = true
                )
            )

            ResponseEntity.status(HttpStatus.CREATED).body(
                HttpResponse(
                    status = true,
                    message = "ลงทะเบียนผู้ว่าจ้างสำเร็จ",
                    data = mapOf("token" to token)
                )
            )
        } catch (e: BadRequestException) {
            logger.error("Register hirer error: ${e.message}")
            ResponseEntity.badRequest().body(
                HttpResponse(status = false, message = e.message ?: "ลงทะเบียนไม่สำเร็จ")
            )
        } catch (e: Exception) {
            logger.error("Register hirer error: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpResponse(status = false, message = "เกิดข้อผิดพลาดในการลงทะเบียน")
            )
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<HttpResponse> {
        return try {
            val token = authService.login(request)
            ResponseEntity.ok(
                HttpResponse(
                    status = true,
                    message = "เข้าสู่ระบบสำเร็จ",
                    data = mapOf("token" to token)
                )
            )
        } catch (e: BadCredentialsException) {
            logger.error("Login error: ${e.message}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                HttpResponse(status = false, message = "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง")
            )
        } catch (e: Exception) {
            logger.error("Login error: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpResponse(status = false, message = "เกิดข้อผิดพลาดในการเข้าสู่ระบบ")
            )
        }
    }

    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal personPrincipal: PersonPrincipal): ResponseEntity<HttpResponse> {
        return try {
            authService.logout(personPrincipal.getPersonId())
            ResponseEntity.ok(
                HttpResponse(status = true, message = "ออกจากระบบสำเร็จ")
            )
        } catch (e: Exception) {
            logger.error("Logout error: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpResponse(status = false, message = "เกิดข้อผิดพลาดในการออกจากระบบ")
            )
        }
    }

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal personPrincipal: PersonPrincipal): ResponseEntity<HttpResponse> {
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
        } catch (e: NotFoundException) {
            logger.error("Get current user error: ${e.message}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                HttpResponse(status = false, message = e.message ?: "ไม่พบผู้ใช้")
            )
        } catch (e: Exception) {
            logger.error("Get current user error: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                HttpResponse(status = false, message = "เกิดข้อผิดพลาดในการดึงข้อมูล")
            )
        }
    }
}

