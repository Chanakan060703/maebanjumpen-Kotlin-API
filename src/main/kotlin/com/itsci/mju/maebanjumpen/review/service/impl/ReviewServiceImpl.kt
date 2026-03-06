package com.itsci.mju.maebanjumpen.review.service.impl

import com.itsci.mju.maebanjumpen.common.exception.BadRequestException
import com.itsci.mju.maebanjumpen.entity.Review
import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.hire.repository.HireRepository
import com.itsci.mju.maebanjumpen.hire.service.impl.HireStatusUpdateService
import com.itsci.mju.maebanjumpen.partyrole.service.HousekeeperService
import com.itsci.mju.maebanjumpen.review.dto.ReviewDTO
import com.itsci.mju.maebanjumpen.review.repository.ReviewRepository
import com.itsci.mju.maebanjumpen.review.request.CreateReviewRequest
import com.itsci.mju.maebanjumpen.review.request.UpdateReviewRequest
import com.itsci.mju.maebanjumpen.review.service.ReviewService
import com.itsci.mju.maebanjumpen.common.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ReviewServiceImpl @Autowired internal constructor(
    private val reviewRepository: ReviewRepository,
    private val hireRepository: HireRepository,
    private val housekeeperService: HousekeeperService,
    private val hireStatusUpdateService: HireStatusUpdateService,
) : ReviewService {
  override fun listAllReviews(): List<ReviewDTO> {
    return reviewRepository.findAll().map { review ->
      ReviewDTO(
          id = review.id,
          reviewMessage = review.reviewMessage,
          score = review.score,
          reviewDate = review.reviewDate,
          hires = review.hire?.let { HireDTO(it.id, it.hireName, it.jobStatus) }
      )
    }
  }

  override fun getReviewById(id: Long): ReviewDTO? {
    val review = reviewRepository.findById(id)
      .orElseThrow { NotFoundException("Review not found with id: $id") }

    return ReviewDTO(
        id = review.id,
        reviewMessage = review.reviewMessage,
        score = review.score,
        reviewDate = review.reviewDate,
        hires = review.hire?.let { HireDTO(it.id, it.hireName, it.jobStatus) }
    )
  }

  @Transactional
  override fun createReview(request: CreateReviewRequest): ReviewDTO {
    val hire = hireRepository.findById(request.hireId)
      .orElseThrow { NotFoundException("Hire not found with id: ${request.hireId}") }

    val review = Review(
        reviewMessage = request.reviewMessage,
        score = request.score,
        reviewDate = LocalDateTime.now(),
        hire = hire
    )
    val savedReview = reviewRepository.save(review)
    return ReviewDTO(
        id = savedReview.id,
        reviewMessage = savedReview.reviewMessage,
        score = savedReview.score,
        reviewDate = savedReview.reviewDate,
        hires = savedReview.hire?.let { HireDTO(it.id, it.hireName, it.jobStatus) }
    )

  }

  @Transactional
  override fun deleteReview(id: Long): Boolean {
    val review = reviewRepository.findById(id)
      .orElseThrow { NotFoundException("Review not found with id: $id") }

    if (review.isDelete == true) {
      throw BadRequestException("Review ถูกลบไปแล้ว")
    }

    review.isDelete = true
    reviewRepository.save(review)

    return true
  }

  override fun getReviewByHireId(hireId: Long): ReviewDTO? {
    val review = reviewRepository.findByHireId(hireId)
      .orElseThrow { NotFoundException("Review not found with hire id: $hireId") }
    return ReviewDTO(
        id = review.id,
        reviewMessage = review.reviewMessage,
        score = review.score,
        reviewDate = review.reviewDate,
        hires = review.hire?.let { HireDTO(it.id, it.hireName, it.jobStatus) }
    )
  }

  @Transactional
  override fun updateReview(request: UpdateReviewRequest): ReviewDTO {
    val review = reviewRepository.findById(request.id)
        .orElseThrow { NotFoundException("Review not found with id: ${request.id}") }

    request.reviewMessage?.let { review.reviewMessage = it }
    request.score?.let { review.score = it }

    val savedReview = reviewRepository.save(review)
    return ReviewDTO(
        id = savedReview.id,
        reviewMessage = savedReview.reviewMessage,
        score = savedReview.score,
        reviewDate = savedReview.reviewDate,
        hires = savedReview.hire?.let { HireDTO(it.id, it.hireName, it.jobStatus) }
    )
  }

  override fun getReviewsByHireId(hireId: Long): List<ReviewDTO> {
    return reviewRepository.findAllByHireId(hireId).map { review ->
      ReviewDTO(
          id = review.id,
          reviewMessage = review.reviewMessage,
          score = review.score,
          reviewDate = review.reviewDate,
          hires = review.hire?.let { HireDTO(it.id, it.hireName, it.jobStatus) }
      )
    }
  }

  override fun getReviewsByHousekeeperId(housekeeperId: Long): List<ReviewDTO> {
    return reviewRepository.findAllByHousekeeperId(housekeeperId).map { review ->
      ReviewDTO(
          id = review.id,
          reviewMessage = review.reviewMessage,
          score = review.score,
          reviewDate = review.reviewDate,
          hires = review.hire?.let { hire -> HireDTO(hire.id, hire.hireName, hire.jobStatus) }
      )
    }
  }
}

