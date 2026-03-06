package com.itsci.mju.maebanjumpen.person.service.impl

import com.itsci.mju.maebanjumpen.entity.Hirer
import com.itsci.mju.maebanjumpen.entity.Housekeeper
import com.itsci.mju.maebanjumpen.entity.Admin
import com.itsci.mju.maebanjumpen.partyrole.constant.RoleEnum
import com.itsci.mju.maebanjumpen.partyrole.repository.HirerRepository
import com.itsci.mju.maebanjumpen.partyrole.repository.HousekeeperRepository
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository
import com.itsci.mju.maebanjumpen.person.dto.PersonPrincipal
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("personDetailServiceImpl")
class PersonDetailServiceImpl @Autowired internal constructor(
    private val personRepository: PersonRepository,
    private val partyRoleRepository: PartyRoleRepository,
    private val housekeeperRepository: HousekeeperRepository,
    private val hirerRepository: HirerRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val personOpt = personRepository.findByUsername(username)

        if (personOpt.isEmpty) {
            throw UsernameNotFoundException("User not found with username: $username")
        }

        val person = personOpt.get()
        val personPrincipal = PersonPrincipal()

        personPrincipal.setPersonId(person.id ?: 0)
        personPrincipal.setEmail(person.email)
        personPrincipal.setUsername(person.username)
        personPrincipal.setPassword(person.password)
        personPrincipal.setFirstName(person.firstName)
        personPrincipal.setLastName(person.lastName)
        personPrincipal.setAccountEnabled(person.accountStatus == "ACTIVE")

        // Find party role and set role
        val partyRoles = partyRoleRepository.findByPersonId(person.id ?: 0)

        if (partyRoles.isNotEmpty()) {
            val partyRole = partyRoles.first()
            personPrincipal.setPartyRoleId(partyRole.id)

            val role = when (partyRole) {
                is Housekeeper -> RoleEnum.HOUSEKEEPER.securityRole
                is Hirer -> RoleEnum.HIRER.securityRole
                is Admin -> RoleEnum.ADMIN.securityRole
                else -> null
            }
            personPrincipal.setRole(role)
        }

        return personPrincipal
    }
}