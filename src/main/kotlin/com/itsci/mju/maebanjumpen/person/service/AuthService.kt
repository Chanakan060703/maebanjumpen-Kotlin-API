package com.itsci.mju.maebanjumpen.person.service

import com.itsci.mju.maebanjumpen.person.dto.PersonPrincipal
import com.itsci.mju.maebanjumpen.person.request.LoginRequest
import com.itsci.mju.maebanjumpen.person.request.RegisterHirerRequest
import com.itsci.mju.maebanjumpen.person.request.RegisterHousekeeperRequest

interface AuthService {
    fun registerHousekeeper(request: RegisterHousekeeperRequest): PersonPrincipal
    fun registerHirer(request: RegisterHirerRequest): PersonPrincipal
    fun login(request: LoginRequest): String
    fun logout(personId: Long)
    fun getPersonPrincipal(personId: Long): PersonPrincipal
}

