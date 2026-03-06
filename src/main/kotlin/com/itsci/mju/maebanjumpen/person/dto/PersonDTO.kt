package com.itsci.mju.maebanjumpen.person.dto

data class PersonDTO(
    var id: Long? = 0,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var pictureUrl: String? = null,
    var accountStatus: String? = null,

)

data class PersonDetailDTO(
    var id: Long? = 0,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var pictureUrl: String? = null,
    var accountStatus: String? = null,
)

data class PersonMeDto(
    var id: Long? = 0,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var pictureUrl: String? = null,
)

data class UpdateMeDto(
    var id: Long? = 0,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var pictureUrl: String? = null,
)

data class UpdatePasswordDto(
    var id: Long? = 0,
    var oldPassword: String? = null,
    var newPassword: String? = null,
)

data class EditAdminDto(
    var id: Long? = 0,
    var accountStatus: String? = null,
)

data class AdminListDto(
    var id: Long? = 0,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var accountStatus: String? = null,
)

data class AccountManagerListDto(
    var id: Long? = 0,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var accountStatus: String? = null,
)
