package com.itsci.mju.maebanjumpen.partyrole.dto

class AccountManagerDTO : PartyRoleDTO() {
    var managerID: Long? = null

    override fun getType(): String = "accountManager"
}

