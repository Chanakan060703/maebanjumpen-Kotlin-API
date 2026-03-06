package com.itsci.mju.maebanjumpen.person.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterHirerRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    val username: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String,

    @field:Email(message = "Email must be valid")
    val email: String? = null,

    @field:NotBlank(message = "First name is required")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    val lastName: String,

    val phoneNumber: String? = null,

    val address: String? = null,

    val idCardNumber: String? = null
)

