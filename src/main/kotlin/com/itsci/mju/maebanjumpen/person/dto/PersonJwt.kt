package com.itsci.mju.maebanjumpen.person.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PersonJwt(
    val uid: Long? = null,
    val email: String? = null,
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null,
    val partyRoleId: Long? = null
)
