package com.itsci.mju.maebanjumpen.person.service.impl

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import com.itsci.mju.maebanjumpen.entity.Admin
import com.itsci.mju.maebanjumpen.entity.Person
import com.itsci.mju.maebanjumpen.partyrole.repository.AdminRepository
import com.itsci.mju.maebanjumpen.person.dto.*
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import com.itsci.mju.maebanjumpen.person.service.PersonService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PersonServiceImpl(
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val personRepository: PersonRepository,
    private val adminRepository: AdminRepository
) : PersonService {

    override fun getUserById(userId: Long): PersonMeDto {
        val person = personRepository.findById(userId)
            .orElseThrow { NotFoundException("Person not found with id: $userId") }

        return PersonMeDto(
            id = person.id,
            email = person.email,
            firstName = person.firstName,
            lastName = person.lastName,
            idCardNumber = person.idCardNumber,
            phoneNumber = person.phoneNumber,
            address = person.address,
            pictureUrl = person.pictureUrl
        )
    }

    override fun createAdmin(createAdmin: CreateAdminDto, userType: Int) {
        // Check if username already exists
        if (personRepository.findByUsername(createAdmin.username).isPresent) {
            throw BadRequestException("Username already exists")
        }
        if (createAdmin.password.isEmpty()) {
            throw BadRequestException("Password cannot be empty")
        }

        // Create Person
        val person = Person(
            username = createAdmin.username,
            password = bCryptPasswordEncoder.encode(createAdmin.password),
            firstName = createAdmin.firstName,
            lastName = createAdmin.lastName,
            email = createAdmin.email,
            phoneNumber = createAdmin.phoneNumber,
            address = createAdmin.address,
            idCardNumber = createAdmin.idCardNumber,
            accountStatus = "ACTIVE",
            createAt = LocalDateTime.now(),
            updateAt = LocalDateTime.now()
        )
        val savedPerson = personRepository.save(person)

        // Create Admin party role
        val admin = Admin().apply {
            this.person = savedPerson
        }
        adminRepository.save(admin)
    }

    override fun findAllAdmin(
        pageable: Pageable,
        ascending: Boolean,
        searchTerm: String?,
        sortField: String?
    ): Page<AdminListDto> {
        // Simple implementation - get all admins
        val admins = adminRepository.findAll()
        val adminDtos = admins.mapNotNull { admin ->
            admin.person?.let { person ->
                AdminListDto(
                    id = person.id,
                    email = person.email,
                    firstName = person.firstName,
                    lastName = person.lastName,
                    accountStatus = person.accountStatus
                )
            }
        }
        return PageImpl(adminDtos, pageable, adminDtos.size.toLong())
    }

    override fun deleteAdmin(id: Int) {
        personRepository.deleteById(id.toLong())
    }

    override fun updateUserMe(id: Int, updateUserDto: UpdateMeDto) {
        val person = personRepository.findById(id.toLong())
            .orElseThrow { NotFoundException("Person not found") }

        person.firstName = updateUserDto.firstName
        person.lastName = updateUserDto.lastName
        person.phoneNumber = updateUserDto.phoneNumber
        person.updateAt = LocalDateTime.now()

        personRepository.save(person)
    }

    override fun updatePassword(id: Int, updatePasswordDto: UpdatePasswordDto) {
        val person = personRepository.findById(id.toLong())
            .orElseThrow { NotFoundException("Person not found") }

        if (bCryptPasswordEncoder.matches(updatePasswordDto.oldPassword, person.password)) {
            person.password = bCryptPasswordEncoder.encode(updatePasswordDto.newPassword)
            person.updateAt = LocalDateTime.now()
            personRepository.save(person)
        } else {
            throw BadRequestException("Old password is incorrect")
        }
    }

    override fun editAdmin(id: Int, editAdminDto: EditAdminDto) {
        val person = personRepository.findById(id.toLong())
            .orElseThrow { NotFoundException("Person not found") }

        person.accountStatus = editAdminDto.accountStatus
        person.updateAt = LocalDateTime.now()
        personRepository.save(person)
    }
}