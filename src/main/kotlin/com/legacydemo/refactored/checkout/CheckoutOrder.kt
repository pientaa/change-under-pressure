package com.legacydemo.refactored.checkout

import com.legacydemo.shared.Money
import com.legacydemo.shared.OrderStatus
import com.legacydemo.shared.ShippingMethod

data class CheckoutOrder(
    val id: String,
    val customerId: String,
    val basePrice: Money,
    val campaignCode: String? = null,
    val shippingMethod: ShippingMethod = ShippingMethod.STANDARD,
    var status: OrderStatus = OrderStatus.NEW
)
