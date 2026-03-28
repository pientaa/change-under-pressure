package com.legacydemo.checkout.legacy

import com.legacydemo.campaign.CampaignCatalog
import com.legacydemo.campaign.CampaignCode
import com.legacydemo.customer.CustomerProfileService
import com.legacydemo.repo.OrderRepository
import com.legacydemo.shared.Money
import com.legacydemo.shared.OrderStatus
import java.math.BigDecimal

class PlaceOrderService(
    private val customerProfileService: CustomerProfileService,
    private val campaignCatalog: CampaignCatalog,
    private val orderRepository: OrderRepository
) {

    fun placeOrder(order: Order): Order {
        // --- enrich customer info ---
        val profile = customerProfileService.findProfile(order.customerId)
        order.vip = profile.vip
        order.segment = profile.segment

        // --- apply campaign ---
        val campaign = order.campaignCode?.let { campaignCatalog.findCampaign(CampaignCode(it)) }
        val discount = campaign?.discountPercent ?: BigDecimal.ZERO
        order.finalPrice = if (discount > BigDecimal.ZERO) {
            val factor = BigDecimal.ONE - discount.divide(BigDecimal(100))
            order.basePrice * factor
        } else {
            order.basePrice
        }

        // --- determine priority ---
        order.priority = order.vip
        // Point X: VIP + PARTNER2026 + high value + non-economy => priority
        if (order.vip
            && order.campaignCode == "PARTNER2026"
            && order.finalPrice >= Money("100.00")
            && order.shippingMethod != com.legacydemo.shared.ShippingMethod.ECONOMY
        ) {
            order.priority = true
        }

        // --- assign warehouse ---
        order.warehouseId = if (order.priority) "WH-PRIORITY" else "WH-STANDARD"

        // --- save ---
        order.status = OrderStatus.CONFIRMED
        return orderRepository.save(order)
    }
}

