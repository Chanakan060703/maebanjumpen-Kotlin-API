package com.itsci.mju.maebanjumpen.person.service.impl

import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PersonRepositoryImpl : UserRepositoryCustom {

  @PersistenceContext
  lateinit var entityManager: EntityManager
  lateinit var queryFactory: JPAQueryFactory

  private val qUser = QUser.user
  private val qUserType = QUserType.userType

  @PostConstruct
  fun init() {
    queryFactory = JPAQueryFactory(entityManager)
  }

  override fun getUserById(userId: Long): UserMeDto {
    val criteria = qUser.id.eq(userId)
    val query = queryFactory.select(qUser)
      .from(qUser)
      .leftJoin(qUserType).on(qUserType.id.eq(qUser.userTypeId))
      .where(criteria)
    return query.fetchOne()?.let { user ->
      UserMeDto(
        user.id,
        user.fullName,
        user.email,
        user.phoneNumber,
        user.imageUrl,
        user.emailVerified,
        user.provider ?: "",
        user.platformId,
      )
    } ?: throw Exception("User not found")
  }

  override fun findAllAdmin(
    pageable: Pageable,
    ascending: Boolean,
    searchTerm: String?,
    sortField: String?
  ): Page<AdminListDto> {
    val criteria = qUser.userTypeId.eq(2)

    val query = queryFactory.select(
      QAdminListDto(
        qUser.id,
        qUser.createdDate,
        qUser.firstName?.concat(" ")?.concat(qUser.lastName),
        qUser.email,
        qUser.status,
      )
    ).from(qUser)
      .where(criteria)
      .groupBy(
        qUser.id,
        qUser.createdDate,
        qUser.firstName,
        qUser.email,
        qUser.status,
      )
      .offset(pageable.offset)
      .limit(pageable.pageSize.toLong())

    if (ascending) {
      if (!sortField.isNullOrEmpty()) {
        if (sortField == "createdDate") {
          query.orderBy(qUser.createdDate.asc())
        } else if (sortField == "name") {
          query.orderBy(qUser.firstName.asc())
        } else if (sortField == "email") {
          query.orderBy(qUser.email.asc())
        } else if (sortField == "status") {
          query.orderBy(qUser.status.asc())
        }
      } else {
        query.orderBy(qUser.id.asc())
      }
    } else  {
      if (!sortField.isNullOrEmpty()) {
        if (sortField == "createdDate") {
          query.orderBy(qUser.createdDate.asc())
        } else if (sortField == "name") {
          query.orderBy(qUser.firstName.asc())
        } else if (sortField == "email") {
          query.orderBy(qUser.email.asc())
        } else if (sortField == "status") {
          query.orderBy(qUser.status.asc())
        }
      } else {
        query.orderBy(qUser.id.desc())
      }
    }
    return PageImpl(query.fetch(), pageable, query.fetchCount())
  }

}