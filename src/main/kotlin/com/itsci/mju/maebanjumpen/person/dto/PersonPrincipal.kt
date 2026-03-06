package com.itsci.mju.maebanjumpen.person.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User


class PersonPrincipal: UserDetails, OAuth2User, OidcUser{
  private var personId: Long = 0
  private var username: String? = null
  private var password: String? = null
  private var companyId: Long? = 0
  private var personType: String? = null
  private var name: String? = null
  private var attributes: MutableMap<String, Any>? = null
  private var personTypeId: Long? = null
  private var path: String? = null
  private var email: String? = null
  private var claims: MutableMap<String, Any>? = null
  private var personInfo: OidcUserInfo? = null
  private var idToken: OidcIdToken? = null
  private var emailVerify: Boolean? = false
  private var expired: Long? = null
}