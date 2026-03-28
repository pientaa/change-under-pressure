package com.legacydemo.fulfillment

import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod

data class PriorityInput(
    val vip: Boolean,
    val campaignCode: String?,
    val finalPrice: Money,
    val shippingMethod: ShippingMethod
)

data class PriorityDecision(
    val priority: Boolean
)

interface FulfillmentApi {
    fun determinePriority(input: PriorityInput): PriorityDecision
}

