package com.itsci.mju.maebanjumpen.person.repository

import com.itsci.mju.maebanjumpen.entity.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PersonRepository : JpaRepository<Person, Long>, PersonRepositoryCustom {
  fun findByEmail(username: String): Optional<Person>
  fun findByUsername(username: String): Optional<Person>

  @Query("select * from users where username = :username or email = :username", nativeQuery = true)
  fun findByUsernameAndEmail(username: String): Optional<Person>
}