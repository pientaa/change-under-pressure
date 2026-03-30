package com.legacydemo.shared.campaign

import java.math.BigDecimal

data class CampaignCode(val value: String)

data class Campaign(
    val code: CampaignCode,
    val description: String,
    val discountPercent: BigDecimal
)

class CampaignCatalog(
    private val campaigns: Map<CampaignCode, Campaign>
) {
    fun findCampaign(code: CampaignCode): Campaign? = campaigns[code]

    companion object {
        val PARTNER2026 = CampaignCode("PARTNER2026")

        fun withDefaults(): CampaignCatalog = CampaignCatalog(
            mapOf(
                PARTNER2026 to Campaign(
                    code = PARTNER2026,
                    description = "Partner campaign 2026",
                    discountPercent = BigDecimal("10")
                )
            )
        )
    }
}

