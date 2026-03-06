package com.itsci.mju.maebanjumpen.review.repository

import com.itsci.mju.maebanjumpen.entity.Review
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {

    companion object {
        const val REVIEW_GRAPH = "review-with-hire-details"
    }

    @EntityGraph(attributePaths = ["hire"])
    fun findByHireId(hireId: Long): Optional<Review>

    @EntityGraph(attributePaths = ["hire"])
    fun findAllByHireId(hireId: Long): List<Review>

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.hire h WHERE h.housekeeper.id = :housekeeperId")
    fun findAllByHousekeeperId(@Param("housekeeperId") housekeeperId: Long): List<Review>
}

