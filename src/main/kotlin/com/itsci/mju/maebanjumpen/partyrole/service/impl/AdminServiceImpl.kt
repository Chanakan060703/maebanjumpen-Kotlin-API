package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.entity.Admin
import com.itsci.mju.maebanjumpen.partyrole.dto.AdminDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.AdminRepository
import com.itsci.mju.maebanjumpen.partyrole.service.AdminService
import org.springframework.stereotype.Service

@Service
class AdminServiceImpl(
    private val adminRepository: AdminRepository
) : AdminService {

    private fun mapAdminToDto(admin: Admin): AdminDTO {
        return AdminDTO().apply {
            id = admin.id
        }
    }

    override fun listAllAdmins(): List<AdminDTO> {
        return adminRepository.findAll().map { mapAdminToDto(it) }
    }

    override fun getAdminById(id: Int): AdminDTO {
        val entity = adminRepository.findById(id)
            .orElseThrow { NoSuchElementException("Admin not found with ID: $id") }
        return mapAdminToDto(entity)
    }

    override fun createAdmin(adminDto: AdminDTO): AdminDTO {
        val admin = Admin()
        val savedEntity = adminRepository.save(admin)
        return mapAdminToDto(savedEntity)
    }

    override fun deleteAdmin(id: Int) {
        if (!adminRepository.existsById(id)) {
            throw NoSuchElementException("Admin not found with ID: $id")
        }
        adminRepository.deleteById(id)
    }

    override fun updateAdmin(id: Int, adminDto: AdminDTO): AdminDTO {
        val existingAdmin = adminRepository.findById(id)
            .orElseThrow { NoSuchElementException("Admin not found with ID: $id") }
        val updatedEntity = adminRepository.save(existingAdmin)
        return mapAdminToDto(updatedEntity)
    }
}

