package com.legacydemo.legacy

import com.legacydemo.shared.campaign.CampaignCatalog
import com.legacydemo.shared.campaign.CampaignCode
import com.legacydemo.shared.customer.CustomerProfileService
import com.legacydemo.shared.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.OrderStatus
import com.legacydemo.shared.ShippingMethod
import java.math.BigDecimal

/**
 * The legacy "God class" orchestrator — all domain logic inline.
 *
 * This single function contains business rules owned by four different teams:
 *  - Customer enrichment (Customer team)
 *  - Campaign lookup (Campaign team)
 *  - Price calculation with discount (Pricing team)
 *  - Priority routing (Fulfillment team)
 *
 * Any change to pricing rules, fulfillment routing, campaign logic, or customer
 * segments requires editing THIS file — forcing cross-team PRs and merge conflicts.
 *
 * Compare with the refactored version:
 * @see com.legacydemo.refactored.checkout.PlaceOrderService
 */
class PlaceOrderService(
    private val customerProfileService: CustomerProfileService,
    private val campaignCatalog: CampaignCatalog,
    private val orderRepository: OrderRepository
) {

    // slide:demo-00-flow:start
    fun placeOrder(order: Order): Order {

        // ── CUSTOMER DOMAIN (owned by Customer team) ──────────────────
        val profile = customerProfileService.findProfile(order.customerId)
        order.vip = profile.vip
        order.segment = profile.segment

        // ── CAMPAIGN DOMAIN (owned by Campaign team) ──────────────────
        val campaign = order.campaignCode?.let {
            campaignCatalog.findCampaign(CampaignCode(it))
        }

        // ── PRICING DOMAIN (owned by Pricing team) ────────────────────
        // TODO: support multi-currency — currently assumes USD
        if (campaign != null) {
            val discount = campaign.discountPercent
            val factor = BigDecimal.ONE - discount.divide(BigDecimal(100))
            order.finalPrice = order.basePrice * factor
        } else {
            order.finalPrice = order.basePrice
        }

        // ── FULFILLMENT DOMAIN (owned by Fulfillment team) ────────────
        // VIP customers always get priority routing
        if (order.vip) {
            order.priority = true
        }
        // GOLD segment with partner campaign — lower threshold
        // (added Q3 2025 for partner launch — ask Anna before changing)
        else if (order.segment == Segment.GOLD
            && order.campaignCode == "PARTNER2026"
            && order.finalPrice >= Money("50.00")
            && order.shippingMethod != ShippingMethod.ECONOMY
        ) {
            order.priority = true
        }
        // Standard priority — high-value non-economy orders
        else if (order.finalPrice >= Money("100.00")
            && order.shippingMethod != ShippingMethod.ECONOMY
        ) {
            order.priority = true
        }
        // Default — standard routing
        else {
            order.priority = false
        }

        // ── SAVE ──────────────────────────────────────────────────────
        order.status = OrderStatus.CONFIRMED
        return orderRepository.save(order)
    }
    // slide:demo-00-flow:end
}

