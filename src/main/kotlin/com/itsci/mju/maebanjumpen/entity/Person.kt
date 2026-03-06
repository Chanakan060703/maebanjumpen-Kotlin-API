package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "person")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Person(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    @Column(unique = true, nullable = false)
    var username: String? = null,

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null,

    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var pictureUrl: String? = null,
    var accountStatus: String? = null,

    @Column(name = "create_at")
    var createAt: LocalDateTime? = null,

    @Column(name = "update_at")
    var updateAt: LocalDateTime? = null,

    @Column(name = "is_delete")
    var isDelete: Boolean? = false
)

