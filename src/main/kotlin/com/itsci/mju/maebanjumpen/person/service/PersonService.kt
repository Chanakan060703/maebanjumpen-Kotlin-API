package com.itsci.mju.maebanjumpen.person.service

import com.itsci.mju.maebanjumpen.person.dto.AdminListDto
import com.itsci.mju.maebanjumpen.person.dto.CreateAdminDto
import com.itsci.mju.maebanjumpen.person.dto.EditAdminDto
import com.itsci.mju.maebanjumpen.person.dto.PersonMeDto
import com.itsci.mju.maebanjumpen.person.dto.UpdateMeDto
import com.itsci.mju.maebanjumpen.person.dto.UpdatePasswordDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PersonService {
  fun getUserById(personId: Long): PersonMeDto
  fun createAdmin(createAdmin: CreateAdminDto, userType: Int)

  fun findAllAdmin(
    pageable: Pageable,
    ascending: Boolean,
    searchTerm: String?,
    sortField: String?,
  ): Page<AdminListDto>

  fun deleteAdmin(id: Int): Boolean
  fun updateUserMe(id: Int, updateUserDto: UpdateMeDto)
  fun updatePassword(id: Int, updatePasswordDto: UpdatePasswordDto)
  fun editAdmin(id: Int, editAdminDto: EditAdminDto)
}