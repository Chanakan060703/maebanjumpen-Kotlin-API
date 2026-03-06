package com.itsci.mju.maebanjumpen.person.service.impl


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class PersonServiceImpl @Autowired constructor(
  private val bCryptPasswordEncoder: BCryptPasswordEncoder,
  private val userRepository: UserRepository,
  private val platformRepository: PlatformRepository,
  private val userTypeRepository: UserTypeRepository,
  cryptPasswordEncoder: BCryptPasswordEncoder,
) : UserService {

  override fun getUserById(userId: Long): UserMeDto {
    return userRepository.getUserById(userId)
  }

  override fun createAdmin(createAdmin: CreateAdminDto, userType: Int) {

    val user = userRepository.findByUsernameAndEmail(createAdmin.username)

    if (user.isPresent) {
      throw BadRequestException("Username is already exist")
    }
    if (createAdmin.password.isEmpty()) {
      throw BadRequestException("Password is not empty")
    }
    platformRepository.findById(createAdmin.platform).orElseThrow{
      throw NotFoundException("Not found this Platform")
    }

    val userData = User(
      username = createAdmin.username,
      password = bCryptPasswordEncoder.encode(createAdmin.password),
      firstName = createAdmin.firstName,
      lastName = createAdmin.lastName,
      emailVerified = true,
      email = createAdmin.email,
      status = true,
      phoneVerified = false,
      userTypeId = userType.toLong(),
      platformId = createAdmin.platform
    )

    userRepository.save(userData)

  }

  override fun findAllAdmin(
    pageable: Pageable,
    ascending: Boolean,
    searchTerm: String?,
    sortField: String?
  ): Page<AdminListDto> {
    return userRepository.findAllAdmin(
      pageable,
      ascending,
      searchTerm,
      sortField,
    )
  }

  override fun deleteAdmin(id: Int) {
    userRepository.deleteById(id.toLong())
  }

  override fun updateUserMe(id: Int, updateUserDto: UpdateMeDto) {
    val user = userRepository.findById(id.toLong()).orElseThrow{
      NotFoundException("User does not exist")
    }

    user.firstName = updateUserDto.firstName
    user.lastName = updateUserDto.lastName
    user.phoneNumber = updateUserDto.phoneNumber

    userRepository.save(user)
  }

  override fun updatePassword(id: Int, updatePasswordDto: UpdatePasswordDto) {
    val user = userRepository.findById(id.toLong()).orElseThrow{
      NotFoundException("User does not found")
    }

    if (bCryptPasswordEncoder.matches(updatePasswordDto.oldPassword, user.password)) {
      user.password = bCryptPasswordEncoder.encode(updatePasswordDto.newPassword)
      userRepository.save(user)
    } else {
      throw BadRequestException("Old password is incorrect")
    }
  }

  override fun editAdmin(id: Int, editAdminDto: EditAdminDto) {
    val user = userRepository.findById(id.toLong()).orElseThrow{
      throw NotFoundException("User does not found")
    }

    if (user.userType!!.id != UserRoleEnum.ROLE_ADMIN.value) {
      throw NotFoundException("User is not Admin")
    }

    user.firstName = editAdminDto.firstName
    user.lastName = editAdminDto.lastName

    userRepository.save(user)
  }

  fun isValidEmail(str: String): Boolean {
    return Pattern.compile(
      "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
          "\\@" +
          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
          "(" +
          "\\." +
          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
          ")+"
    ).matcher(str).matches()
  }
}