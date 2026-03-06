package com.itsci.mju.maebanjumpen.person.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User


class PersonPrincipal : UserDetails, OAuth2User, OidcUser {
    private var personId: Long = 0
    private var _username: String? = null
    private var _password: String? = null
    private var partyRoleId: Long? = null
    private var role: String? = null
    private var _name: String? = null
    private var _attributes: MutableMap<String, Any>? = null
    private var _email: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var _claims: MutableMap<String, Any>? = null
    private var personInfo: OidcUserInfo? = null
    private var _idToken: OidcIdToken? = null
    private var emailVerified: Boolean = false
    private var accountEnabled: Boolean = true

    // Getters
    fun getPersonId(): Long = personId
    fun getPartyRoleId(): Long? = partyRoleId
    fun getRole(): String? = role
    override fun getEmail(): String? = _email
    fun getFirstName(): String? = firstName
    fun getLastName(): String? = lastName
    fun isEmailVerified(): Boolean = emailVerified

    // Setters
    fun setPersonId(personId: Long) { this.personId = personId }
    fun setPartyRoleId(partyRoleId: Long?) { this.partyRoleId = partyRoleId }
    fun setRole(role: String?) { this.role = role }
    fun setEmail(email: String?) { this._email = email }
    fun setFirstName(firstName: String?) { this.firstName = firstName }
    fun setLastName(lastName: String?) { this.lastName = lastName }
    fun setEmailVerified(emailVerified: Boolean) { this.emailVerified = emailVerified }
    fun setUsername(username: String?) { this._username = username }
    fun setPassword(password: String?) { this._password = password }
    fun setAccountEnabled(enabled: Boolean) { this.accountEnabled = enabled }

    // UserDetails implementation
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return role?.let { mutableListOf(SimpleGrantedAuthority(it)) } ?: mutableListOf()
    }

    override fun getPassword(): String? = _password

    override fun getUsername(): String? = _username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = accountEnabled

    // OAuth2User implementation
    override fun getName(): String? = _name

    override fun getAttributes(): MutableMap<String, Any>? = _attributes

    // OidcUser implementation
    override fun getClaims(): MutableMap<String, Any>? = _claims

    override fun getUserInfo(): OidcUserInfo? = personInfo

    override fun getIdToken(): OidcIdToken? = _idToken
}