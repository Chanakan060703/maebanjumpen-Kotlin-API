package com.itsci.mju.maebanjumpen.partyrole.constant

enum class RoleEnum (val role: String){
  HIRER("hirer"),
  HOUSEKEEPER("housekeeper"),
  ADMIN("admin"),
  ACCOUNT_MANAGER("account manager");

  companion object {
    fun fromString(role: String): RoleEnum? {
      return RoleEnum.entries.find { it.role.equals(role, ignoreCase = true) }
    }
  }
}