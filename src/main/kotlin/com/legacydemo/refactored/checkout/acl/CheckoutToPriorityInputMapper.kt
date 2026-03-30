package com.legacydemo.refactored.checkout.acl

import com.legacydemo.refactored.checkout.CheckoutOrder
import com.legacydemo.shared.customer.CustomerProfile
import com.legacydemo.refactored.fulfillment.PriorityInput
import com.legacydemo.refactored.pricing.PricingResult

object CheckoutToPriorityInputMapper {
    fun map(order: CheckoutOrder, profile: CustomerProfile, pricingResult: PricingResult) =
        PriorityInput(
            vip = profile.vip,
            segment = profile.segment,
            campaignCode = order.campaignCode,
            finalPrice = pricingResult.finalPrice,
            shippingMethod = order.shippingMethod
        )
}
