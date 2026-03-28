package com.legacydemo.fulfillment

import com.legacydemo.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod

/**
 * Input model for the Fulfillment bounded context.
 * Contains only the data the Fulfillment team owns or needs — no legacy Order leaking in.
 */
data class PriorityInput(
    val vip: Boolean,
    val segment: Segment,
    val campaignCode: String?,
    val finalPrice: Money,
    val shippingMethod: ShippingMethod
)

data class PriorityDecision(
    val priority: Boolean
)

/**
 * Team API contract for the Fulfillment team.
 * Consumers pass a [PriorityInput]; the implementation decides priority routing.
 */
interface FulfillmentApi {
    fun determinePriority(input: PriorityInput): PriorityDecision
}

