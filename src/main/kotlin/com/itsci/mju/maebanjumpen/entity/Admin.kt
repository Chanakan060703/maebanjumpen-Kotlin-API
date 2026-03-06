package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@DiscriminatorValue("admin")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
open class Admin : PartyRole() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Admin) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int = super.hashCode()
}

