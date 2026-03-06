package com.lucablock.backofficeapi.user.repository

import com.lucablock.backofficeapi.entity.UserType
import org.springframework.data.jpa.repository.JpaRepository

interface PersonTypeRepository: JpaRepository<UserType, Long> {
}