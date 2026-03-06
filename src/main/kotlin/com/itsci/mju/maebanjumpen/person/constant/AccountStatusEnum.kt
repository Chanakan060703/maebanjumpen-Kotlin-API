package com.itsci.mju.maebanjumpen.person.constant

enum class AccountStatusEnum (val value: String){
  ACTIVE("active"),
  BANNED("banned"),
  PENDING("pending");

  companion object {
    private val types = AccountStatusEnum.entries.associateBy { it.value }

    fun fromValue(value: String): AccountStatusEnum? {
      return types[value]
    }
  }
}