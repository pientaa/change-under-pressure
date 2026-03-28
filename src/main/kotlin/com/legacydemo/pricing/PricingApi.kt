package com.legacydemo.pricing

import com.legacydemo.shared.Money

data class PricingCommand(
    val basePrice: Money,
    val campaignCode: String?
)

data class PricingResult(
    val finalPrice: Money,
    val appliedDiscount: Boolean
)

interface PricingApi {
    fun calculate(command: PricingCommand): PricingResult
}

