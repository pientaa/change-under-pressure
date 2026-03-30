package com.legacydemo.refactored.pricing

import com.legacydemo.shared.Money

// slide:demo-04-pricing-api:start
interface PricingApi {
    fun calculate(command: PricingCommand): PricingResult
}
// slide:demo-04-pricing-api:end

// slide:demo-04-pricing-command:start
data class PricingCommand(
    val basePrice: Money,
    val campaignCode: String?
)

data class PricingResult(
    val finalPrice: Money,
    val appliedDiscount: Boolean,
    val discountName: String? = null
)
// slide:demo-04-pricing-command:end
