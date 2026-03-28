package com.legacydemo.acl

import com.legacydemo.checkout.legacy.Order
import com.legacydemo.fulfillment.PriorityInput

/**
 * Anti-Corruption Layer: translates the legacy Order model
 * into a PriorityInput that the Fulfillment bounded context understands.
 */
object OrderToPriorityInputMapper {

    fun map(order: Order): PriorityInput = PriorityInput(
        vip = order.vip,
        segment = order.segment,
        campaignCode = order.campaignCode,
        finalPrice = order.finalPrice,
        shippingMethod = order.shippingMethod
    )
}

