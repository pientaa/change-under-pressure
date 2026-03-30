package com.legacydemo.refactored.checkout

import com.legacydemo.shared.customer.CustomerProfile
import com.legacydemo.refactored.fulfillment.PriorityDecision
import com.legacydemo.refactored.pricing.PricingResult
import com.legacydemo.shared.OrderStatus

data class PlaceOrderResult(
    val orderId: String,
    val status: OrderStatus,
    val customer: CustomerProfile,
    val pricing: PricingResult,
    val fulfillment: PriorityDecision
)
