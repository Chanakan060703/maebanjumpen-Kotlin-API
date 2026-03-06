package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.entity.Housekeeper
import com.itsci.mju.maebanjumpen.entity.Member
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.MemberRepository
import com.itsci.mju.maebanjumpen.partyrole.service.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class MemberServiceImpl(
    private val memberRepository: MemberRepository
) : MemberService {

    private fun mapMemberToDto(member: Member): MemberDTO {
        return MemberDTO().apply {
            id = member.id
            balance = member.balance
        }
    }

    @Transactional
    override fun saveMember(memberDto: MemberDTO): MemberDTO {
        val member = Member().apply { balance = memberDto.balance }
        val savedMember = memberRepository.save(member)
        return mapMemberToDto(savedMember)
    }

    @Transactional(readOnly = true)
    override fun getMemberById(id: Long): Optional<MemberDTO> {
        return memberRepository.findById(id)
            .map { mapMemberToDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getAllMembers(): List<MemberDTO> {
        val members = memberRepository.findAll()
        return members.map { mapMemberToDto(it) }
    }

    @Transactional
    override fun updateMember(id: Long, memberDto: MemberDTO): MemberDTO {
        val existingMember = memberRepository.findById(id)
            .orElseThrow { RuntimeException("ไม่พบสมาชิกด้วย ID: $id") }

        memberDto.balance?.let { existingMember.balance = it }

        if (existingMember.person != null && memberDto.person != null) {
            val existingPerson = existingMember.person!!
            existingPerson.email = memberDto.person?.email
            existingPerson.firstName = memberDto.person?.firstName
            existingPerson.lastName = memberDto.person?.lastName
            existingPerson.idCardNumber = memberDto.person?.idCardNumber
            existingPerson.phoneNumber = memberDto.person?.phoneNumber
            existingPerson.address = memberDto.person?.address
            existingPerson.pictureUrl = memberDto.person?.pictureUrl

            if (existingPerson.login != null && memberDto.person?.login?.password != null) {
                existingPerson.login?.password = memberDto.person?.login?.password ?: ""
            }
        }

        if (existingMember is Housekeeper) {
            // Handle Housekeeper-specific fields if needed
        }

        val updatedMember = memberRepository.save(existingMember)
        return mapMemberToDto(updatedMember)
    }

    @Transactional
    override fun deleteMember(id: Long) {
        if (!memberRepository.existsById(id)) {
            throw RuntimeException("ไม่พบสมาชิกด้วย ID: $id")
        }
        memberRepository.deleteById(id)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun deductBalance(memberId: Long, amount: Double): MemberDTO {
        val optionalMember = memberRepository.findByIdWithLock(memberId)

        if (optionalMember.isEmpty) {
            throw RuntimeException("ไม่พบสมาชิกด้วย ID: $memberId")
        }

        val member = optionalMember.get()
        if (member !is Housekeeper) {
            throw IllegalArgumentException("รายการหักยอดเงินนี้ไม่ได้มาจากแม่บ้าน (Housekeeper).")
        }

        val currentBalance = member.balance ?: 0.0
        if (currentBalance < amount) {
            throw IllegalArgumentException(
                "ยอดเงินไม่เพียงพอสำหรับสมาชิก ID: $memberId. " +
                "ยอดเงินปัจจุบัน: $currentBalance, จำนวนเงินที่พยายามหัก: $amount"
            )
        }

        member.balance = currentBalance - amount
        val savedMember = memberRepository.save(member)
        return mapMemberToDto(savedMember)
    }
}

