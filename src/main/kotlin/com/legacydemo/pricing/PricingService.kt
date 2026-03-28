package com.legacydemo.pricing

import com.legacydemo.campaign.CampaignCatalog
import com.legacydemo.campaign.CampaignCode
import java.math.BigDecimal

class PricingService(
    private val campaignCatalog: CampaignCatalog
) : PricingApi {

    override fun calculate(command: PricingCommand): PricingResult {
        val campaign = command.campaignCode?.let {
            campaignCatalog.findCampaign(CampaignCode(it))
        }
        val discount = campaign?.discountPercent ?: BigDecimal.ZERO

        return if (discount > BigDecimal.ZERO) {
            val factor = BigDecimal.ONE - discount.divide(BigDecimal(100))
            PricingResult(
                finalPrice = command.basePrice * factor,
                appliedDiscount = true
            )
        } else {
            PricingResult(
                finalPrice = command.basePrice,
                appliedDiscount = false
            )
        }
    }
}

