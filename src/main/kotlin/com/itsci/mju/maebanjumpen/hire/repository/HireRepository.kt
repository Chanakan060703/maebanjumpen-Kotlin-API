package com.itsci.mju.maebanjumpen.hire.repository

import com.itsci.mju.maebanjumpen.entity.Hire
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HireRepository : JpaRepository<Hire, Long> {

    fun findAllWithDetails(): List<Hire>

    fun fetchByIdWithAllDetails(id: Long): Optional<Hire>

    fun findByHirerIdWithDetails(hirerId: Long): List<Hire>

    fun findByHousekeeperIdWithDetails(housekeeperId: Long): List<Hire>

    fun findByHousekeeperIdAndJobStatusWithDetails(
          housekeeperId: Long,
          jobStatus: String
    ): List<Hire>
}

