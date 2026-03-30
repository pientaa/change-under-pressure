package com.legacydemo.legacy

import com.legacydemo.shared.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.OrderStatus
import com.legacydemo.shared.ShippingMethod

/**
 * The "super-model" — one data class that every domain reads and writes.
 *
 * Problem: fields like [vip], [segment] belong to Customer; [finalPrice] belongs to Pricing;
 * [priority] belongs to Fulfillment. But they all live here,
 * mutated by one orchestrator. Any team that needs to change a field
 * must edit this class AND the orchestrator.
 *
 * Compare with the refactored version:
 * @see com.legacydemo.refactored.checkout.CheckoutOrder
 */
// slide:demo-00-order:start
data class Order(
    val id: String,
    val customerId: String,

    // ── Customer domain ──
    var vip: Boolean = false,
    var segment: Segment = Segment.STANDARD,

    // ── Campaign domain ──
    var campaignCode: String? = null,

    // ── Pricing domain ──
    val basePrice: Money,
    var finalPrice: Money = basePrice,

    // ── Fulfillment domain ──
    val shippingMethod: ShippingMethod = ShippingMethod.STANDARD,
    var priority: Boolean = false,

    // ── Order status ──
    var status: OrderStatus = OrderStatus.NEW
)
// slide:demo-00-order:end

