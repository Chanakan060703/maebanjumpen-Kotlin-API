package com.itsci.mju.maebanjumpen.token.repository
import com.itsci.mju.maebanjumpen.token.dto.TokenDto
import org.springframework.data.repository.CrudRepository

interface TokenRepository : CrudRepository<TokenDto, String>