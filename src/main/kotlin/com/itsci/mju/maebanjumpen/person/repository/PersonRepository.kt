package com.itsci.mju.maebanjumpen.person.repository

import com.itsci.mju.maebanjumpen.entity.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PersonRepository : JpaRepository<Person, Long> {
    fun findByEmail(email: String): Optional<Person>
    fun findByUsername(username: String): Optional<Person>

    @Query("SELECT p FROM Person p WHERE p.username = :usernameOrEmail OR p.email = :usernameOrEmail")
    fun findByUsernameOrEmail(usernameOrEmail: String): Optional<Person>
}