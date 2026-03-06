package com.itsci.mju.maebanjumpen.person.service

import com.lucablock.backofficeapi.user.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {
  fun getUserById(userId: Long): UserMeDto
  fun createAdmin(createAdmin: CreateAdminDto, userType: Int)

  fun findAllAdmin(
    pageable: Pageable,
    ascending: Boolean,
    searchTerm: String?,
    sortField: String?,
  ): Page<AdminListDto>

  fun deleteAdmin(id: Int)
  fun updateUserMe(id: Int, updateUserDto: UpdateMeDto)
  fun updatePassword(id: Int, updatePasswordDto: UpdatePasswordDto)
  fun editAdmin(id: Int, editAdminDto: EditAdminDto)
}