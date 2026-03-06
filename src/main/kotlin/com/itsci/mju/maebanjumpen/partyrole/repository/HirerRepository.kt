package com.itsci.mju.maebanjumpen.partyrole.repository

import com.itsci.mju.maebanjumpen.entity.Hirer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HirerRepository : JpaRepository<Hirer, Long> {

    override fun findAll(): List<Hirer>

    override fun findById(id: Long): Optional<Hirer>
}

