package com.legacydemo.refactored.pricing

import com.legacydemo.shared.campaign.CampaignCatalog
import com.legacydemo.shared.campaign.CampaignCode
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
                appliedDiscount = true,
                discountName = campaign?.description
            )
        } else {
            PricingResult(
                finalPrice = command.basePrice,
                appliedDiscount = false
            )
        }
    }
}
