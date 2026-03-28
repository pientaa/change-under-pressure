package com.legacydemo.pricing

import com.legacydemo.shared.Money

/**
 * Input model for the Pricing bounded context.
 * Contains only pricing-relevant data — no legacy Order leaking in.
 */
data class PricingCommand(
    val basePrice: Money,
    val campaignCode: String?
)

data class PricingResult(
    val finalPrice: Money,
    val appliedDiscount: Boolean
)

/**
 * Team API contract for the Pricing team.
 * Consumers pass a [PricingCommand]; the implementation returns a [PricingResult].
 */
interface PricingApi {
    fun calculate(command: PricingCommand): PricingResult
}

