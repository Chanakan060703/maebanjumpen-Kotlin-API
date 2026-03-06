package com.itsci.mju.maebanjumpen.housekeeperskill.dto

import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import com.itsci.mju.maebanjumpen.review.dto.ReviewDTO

data class HousekeeperDetailDTO(
    var detailId: Long? = null,
    var detailBalance: Double? = null,
    var detailPhotoVerifyUrl: String? = null,
    var detailStatusVerify: String? = null,
    var detailRating: Double? = null,
    var detailDailyRate: Double? = null,
    var detailHousekeeperSkills: Set<HousekeeperSkillDTO>? = null,
    var hires: List<HireDTO>? = null,
    var jobsCompleted: Int = 0,
    var reviews: List<ReviewDTO>? = null
)

