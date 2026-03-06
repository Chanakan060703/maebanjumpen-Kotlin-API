package com.itsci.mju.maebanjumpen.person.service.impl

import com.itsci.mju.maebanjumpen.entity.Person
import com.itsci.mju.maebanjumpen.person.dto.PersonPrincipal
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class PersonDetailServiceImpl : UserDetailsService {

  @Autowired
  lateinit var personRepository: PersonRepository

  override fun loadUserByUsername(username: String): UserDetails {
    val personOpt = personRepository.findByUsername(username)
    if (personOpt.isPresent) {
      val person = personOpt.get()
      val personPrincipal = PersonPrincipal()
      personPrincipal.setPersonId(person.id)
      personPrincipal.email = user.email
      personPrincipal.username = user.username
      personPrincipal.setPassword(user.password)
      personPrincipal.setUserTypeId(user.userTypeId)
      personPrincipal.setEmailVerify(user.emailVerified)
      personPrincipal.setPlatform(user.platformId)
      val userType = when {
        user.userType == null -> {
          UserRoleEnum.ROLE_GUEST.name
        }

        user.userType.isSuperAdmin -> {
          UserRoleEnum.ROLE_SUPER_ADMIN.name
        }

        user.userType.isAdmin -> {
          UserRoleEnum.ROLE_ADMIN.name
        }

        else -> {
          UserRoleEnum.ROLE_USER.name
        }
      }
      userPrincipal.setUserType(userType)
      return userPrincipal
    }

    throw UsernameNotFoundException("Username or Password doesn't match!")
  }
}