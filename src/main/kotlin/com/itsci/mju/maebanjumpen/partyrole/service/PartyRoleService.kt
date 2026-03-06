package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO

interface PartyRoleService {
    fun savePartyRole(partyRoleDto: PartyRoleDTO): PartyRoleDTO
    fun getPartyRoleById(id: Long): PartyRoleDTO?
    fun getAllPartyRoles(): List<PartyRoleDTO>
    fun updatePartyRole(id: Long, partyRoleDto: PartyRoleDTO): PartyRoleDTO
    fun deletePartyRole(id: Long)
}

