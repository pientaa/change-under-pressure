package com.legacydemo.refactored.fulfillment

import com.legacydemo.shared.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod

// slide:demo-03-fulfillment-api:start
interface FulfillmentApi {
    fun determinePriority(input: PriorityInput): PriorityDecision
}
// slide:demo-03-fulfillment-api:end

// slide:demo-03-priority-input:start
data class PriorityInput(
    val vip: Boolean,
    val segment: Segment,
    val campaignCode: String?,
    val finalPrice: Money,
    val shippingMethod: ShippingMethod
)

data class PriorityDecision(
    val priority: Boolean,
    val slaTier: SlaTier = SlaTier.STANDARD
)

enum class SlaTier { STANDARD, EXPEDITED, NEXT_DAY }
// slide:demo-03-priority-input:end
