package com.itsci.mju.maebanjumpen.person.repository

import com.itsci.mju.maebanjumpen.person.dto.AdminListDto
import com.itsci.mju.maebanjumpen.person.dto.PersonMeDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PersonRepositoryCustom {
  fun getUserById(userId: Long): PersonMeDto

  fun findAllAdmin(
    pageable: Pageable,
    ascending: Boolean,
    searchTerm: String?,
    sortField: String?,
  ): Page<AdminListDto>
}