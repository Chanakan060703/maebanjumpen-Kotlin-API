package com.itsci.mju.maebanjumpen.hire.controller


import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.hire.service.HireService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/hires")
class HireController @Autowired internal constructor(
    private val hireService: HireService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

}

