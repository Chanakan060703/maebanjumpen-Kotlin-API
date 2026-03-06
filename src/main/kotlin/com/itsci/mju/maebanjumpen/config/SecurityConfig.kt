package com.itsci.mju.maebanjumpen.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.itsci.mju.maebanjumpen.common.response.HttpResponse
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.io.IOException
import java.io.OutputStream

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    @Qualifier("personDetailServiceImpl")
    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    var authEntryPoint: AuthenticationEntryPoint? = null

    @Component("customAuthenticationEntryPoint")
    class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
        @Throws(IOException::class, ServletException::class)
        override fun commence(
            request: HttpServletRequest?,
            response: HttpServletResponse,
            authException: AuthenticationException?,
        ) {
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.status = HttpServletResponse.SC_FORBIDDEN
            val responseStream: OutputStream = response.outputStream
            val mapper = ObjectMapper()
            mapper.writeValue(
                responseStream,
                HttpResponse(
                    status = false,
                    message = "ไม่มีสิทธิ์เข้าถึง คุณไม่ได้รับอนุญาตให้เข้าถึงทรัพยากรนี้",
                    data = false
                )
            )
            responseStream.flush()
        }
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors {
                it.configurationSource(corsConfigurationSource())
            }
            .csrf {
                it.disable()
            }
            .formLogin {
                it.disable()
            }
            .httpBasic {
                it.disable()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(authEntryPoint)
            }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/swagger-resources/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/swagger-ui/index.html").permitAll()
                    .requestMatchers("/api-docs/**").permitAll()
                    .requestMatchers("/webjars/springfox-swagger-ui/**").permitAll()
                    .requestMatchers("/v2/api-docs/**").permitAll()
                    .requestMatchers("/oauth2/**").permitAll()
                    // Auth endpoints - public
                    .requestMatchers("/api/auth/login").permitAll()
                    .requestMatchers("/api/auth/register/**").permitAll()
                    // Legacy endpoint
                    .requestMatchers("/api/user/login").permitAll()
                    // Maeban endpoints - public for now (can be secured later)
                    .requestMatchers("/maeban/**").permitAll()
                    .requestMatchers("/").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .headers { headersConfigurer ->
                headersConfigurer.frameOptions {
                    it.disable()
                }
            }

        http.addFilterBefore(
            jwtAuthorizationFilter(),
            UsernamePasswordAuthenticationFilter::class.java
        )

        return http.build()
    }

    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.exposedHeaders = listOf("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/swagger-ui/index.html", configuration)
        source.registerCorsConfiguration("/*/**", configuration)
        source.registerCorsConfiguration("/api/redirect/**", configuration)
        source.registerCorsConfiguration("/h2/**", configuration)
        return source
    }

    @Bean
    fun customAuthenticationManager(
        userDetailsService: UserDetailsService,
        encoder: PasswordEncoder,
    ): AuthenticationManager {
        return AuthenticationManager { authentication: Authentication ->
            val username = authentication.principal.toString()
            val password = authentication.credentials.toString()
            val user = userDetailsService.loadUserByUsername(username)
            if (!encoder.matches(password, user.password)) {
                throw BadCredentialsException("Bad credentials")
            }
            if (!user.isEnabled) {
                throw DisabledException("User account is not active")
            }
            UsernamePasswordAuthenticationToken(user, null, user.authorities)
        }
    }

    @Bean
    fun userDetailsService(bCryptPasswordEncoder: BCryptPasswordEncoder): UserDetailsService {
        return userDetailsService
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

    @Bean
    fun jwtAuthorizationFilter(): JWTAuthorizationFilter {
        return JWTAuthorizationFilter()
    }
}