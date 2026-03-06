package com.itsci.mju.maebanjumpen.person.service.impl

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.entity.Hirer
import com.itsci.mju.maebanjumpen.entity.Housekeeper
import com.itsci.mju.maebanjumpen.entity.Person
import com.itsci.mju.maebanjumpen.partyrole.constant.RoleEnum
import com.itsci.mju.maebanjumpen.partyrole.repository.HirerRepository
import com.itsci.mju.maebanjumpen.partyrole.repository.HousekeeperRepository
import com.itsci.mju.maebanjumpen.person.dto.PersonPrincipal
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import com.itsci.mju.maebanjumpen.person.request.LoginRequest
import com.itsci.mju.maebanjumpen.person.request.RegisterHirerRequest
import com.itsci.mju.maebanjumpen.person.request.RegisterHousekeeperRequest
import com.itsci.mju.maebanjumpen.person.service.AuthService
import com.itsci.mju.maebanjumpen.token.JWTTokenProvider
import com.itsci.mju.maebanjumpen.token.dto.TokenDto
import com.itsci.mju.maebanjumpen.token.service.TokenService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthServiceImpl @Autowired internal constructor(
    private val personRepository: PersonRepository,
    private val housekeeperRepository: HousekeeperRepository,
    private val hirerRepository: HirerRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JWTTokenProvider,
    private val tokenService: TokenService
) : AuthService {

    @Transactional
    override fun registerHousekeeper(request: RegisterHousekeeperRequest): PersonPrincipal {
        // Check if username already exists
        if (personRepository.findByUsername(request.username).isPresent) {
            throw BadRequestException("Username '${request.username}' already exists")
        }

        // Create Person
        val person = Person(
            username = request.username,
            password = bCryptPasswordEncoder.encode(request.password),
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            address = request.address,
            idCardNumber = request.idCardNumber,
            accountStatus = "ACTIVE",
            createAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now()
        )
        val savedPerson = personRepository.save(person)

        // Create Housekeeper
        val housekeeper = Housekeeper(
            dailyRate = request.dailyRate?.toString(),
            statusVerify = Housekeeper.VerifyStatus.PENDING.name
        ).apply {
            this.person = savedPerson
            this.balance = 0.0
        }
        val savedHousekeeper = housekeeperRepository.save(housekeeper)

        return createPersonPrincipal(savedPerson, savedHousekeeper.id, RoleEnum.HOUSEKEEPER)
    }

    @Transactional
    override fun registerHirer(request: RegisterHirerRequest): PersonPrincipal {
        // Check if username already exists
        if (personRepository.findByUsername(request.username).isPresent) {
            throw BadRequestException("Username '${request.username}' already exists")
        }

        // Create Person
        val person = Person(
            username = request.username,
            password = bCryptPasswordEncoder.encode(request.password),
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            address = request.address,
            idCardNumber = request.idCardNumber,
            accountStatus = "ACTIVE",
            createAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now()
        )
        val savedPerson = personRepository.save(person)

        // Create Hirer
        val hirer = Hirer().apply {
            this.person = savedPerson
            this.balance = 0.0
        }
        val savedHirer = hirerRepository.save(hirer)

        return createPersonPrincipal(savedPerson, savedHirer.id, RoleEnum.HIRER)
    }

    override fun login(request: LoginRequest): String {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        val personPrincipal = authentication.principal as PersonPrincipal
        val token = jwtTokenProvider.generateToken(personPrincipal)

        // Cache token in Redis
        tokenService.cacheToken(
            TokenDto(
                userId = personPrincipal.getPersonId().toString(),
                token = token,
                multipleLogin = true
            )
        )

        return token
    }

    override fun logout(personId: Long) {
        tokenService.revokeToken(personId)
    }

    override fun getPersonPrincipal(personId: Long): PersonPrincipal {
        val person = personRepository.findById(personId)
            .orElseThrow { NotFoundException("Person not found with id: $personId") }

        // Try to find as Housekeeper first
        val housekeeper = housekeeperRepository.findAll()
            .find { it.person?.id == personId }
        if (housekeeper != null) {
            return createPersonPrincipal(person, housekeeper.id, RoleEnum.HOUSEKEEPER)
        }

        // Try to find as Hirer
        val hirer = hirerRepository.findAll()
            .find { it.person?.id == personId }
        if (hirer != null) {
            return createPersonPrincipal(person, hirer.id, RoleEnum.HIRER)
        }

        // Default - no party role
        return createPersonPrincipal(person, null, null)
    }

    private fun createPersonPrincipal(person: Person, partyRoleId: Long?, role: RoleEnum?): PersonPrincipal {
        return PersonPrincipal().apply {
            setPersonId(person.id ?: 0)
            setUsername(person.username)
            setEmail(person.email)
            setFirstName(person.firstName)
            setLastName(person.lastName)
            setPartyRoleId(partyRoleId)
            setRole(role?.securityRole)
            setAccountEnabled(person.accountStatus == "ACTIVE")
        }
    }
}

