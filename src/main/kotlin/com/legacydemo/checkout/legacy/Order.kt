package com.legacydemo.checkout.legacy

import com.legacydemo.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.OrderStatus
import com.legacydemo.shared.ShippingMethod

data class Order(
    val id: String,
    val customerId: String,
    var vip: Boolean = false,
    var segment: Segment = Segment.STANDARD,
    var campaignCode: String? = null,
    val basePrice: Money,
    var finalPrice: Money = basePrice,
    val shippingMethod: ShippingMethod = ShippingMethod.STANDARD,
    var priority: Boolean = false,
    var warehouseId: String? = null,
    var status: OrderStatus = OrderStatus.NEW
)

