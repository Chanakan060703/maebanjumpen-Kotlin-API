package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import java.util.Optional

interface MemberService {
    fun saveMember(memberDto: MemberDTO): MemberDTO
    fun getMemberById(id: Long): Optional<MemberDTO>
    fun getAllMembers(): List<MemberDTO>
    fun updateMember(id: Long, memberDto: MemberDTO): MemberDTO
    fun deleteMember(id: Long)
    fun deductBalance(memberId: Long, amount: Double): MemberDTO
}

