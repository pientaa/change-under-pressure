package com.legacydemo.refactored.checkout.acl

import com.legacydemo.refactored.checkout.CheckoutOrder
import com.legacydemo.refactored.pricing.PricingCommand

object CheckoutToPricingCommandMapper {
    fun map(order: CheckoutOrder) = PricingCommand(
        basePrice = order.basePrice,
        campaignCode = order.campaignCode
    )
}
