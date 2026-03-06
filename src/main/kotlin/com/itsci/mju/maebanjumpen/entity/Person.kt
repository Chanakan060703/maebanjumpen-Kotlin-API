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
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var username: String? = null,

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null,

    var email: String? = null,

    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Column(name = "id_card_number")
    var idCardNumber: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    var address: String? = null,

    @Column(name = "picture_url")
    var pictureUrl: String? = null,

    @Column(name = "account_status")
    var accountStatus: String? = "ACTIVE",

    @Column(name = "create_at")
    var createAt: LocalDateTime? = null,

    @Column(name = "update_at")
    var updateAt: LocalDateTime? = null,

    @Column(name = "is_delete")
    var isDelete: Boolean? = false
)

