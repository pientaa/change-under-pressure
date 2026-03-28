package com.legacydemo.acl

import com.legacydemo.checkout.legacy.Order
import com.legacydemo.pricing.PricingCommand

/**
 * Anti-Corruption Layer: translates the legacy Order model
 * into a PricingCommand that the Pricing bounded context understands.
 */
object OrderToPricingCommandMapper {

    fun map(order: Order): PricingCommand = PricingCommand(
        basePrice = order.basePrice,
        campaignCode = order.campaignCode
    )
}

