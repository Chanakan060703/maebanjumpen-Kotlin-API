package com.itsci.mju.maebanjumpen.partyrole.constant

enum class RoleEnum(val role: String, val securityRole: String) {
    HIRER("hirer", "ROLE_HIRER"),
    HOUSEKEEPER("housekeeper", "ROLE_HOUSEKEEPER"),
    ADMIN("admin", "ROLE_ADMIN"),
    SUPER_ADMIN("super_admin", "ROLE_SUPER_ADMIN"),
    ACCOUNT_MANAGER("account_manager", "ROLE_ACCOUNT_MANAGER");

    companion object {
        fun fromString(role: String): RoleEnum? {
            return entries.find { it.role.equals(role, ignoreCase = true) }
        }

        fun fromSecurityRole(securityRole: String): RoleEnum? {
            return entries.find { it.securityRole.equals(securityRole, ignoreCase = true) }
        }
    }
}