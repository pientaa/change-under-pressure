package com.legacydemo.checkout.legacy

import com.legacydemo.campaign.CampaignCatalog
import com.legacydemo.campaign.CampaignCode
import com.legacydemo.customer.CustomerProfileService
import com.legacydemo.fulfillment.FulfillmentApi
import com.legacydemo.fulfillment.PriorityInput
import com.legacydemo.repo.OrderRepository
import com.legacydemo.shared.OrderStatus
import java.math.BigDecimal

class PlaceOrderService(
    private val customerProfileService: CustomerProfileService,
    private val campaignCatalog: CampaignCatalog,
    private val fulfillmentApi: FulfillmentApi,
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

        // --- determine priority (via Fulfillment Team API) ---
        val priorityInput = PriorityInput(
            vip = order.vip,
            campaignCode = order.campaignCode,
            finalPrice = order.finalPrice,
            shippingMethod = order.shippingMethod
        )
        val decision = fulfillmentApi.determinePriority(priorityInput)
        order.priority = decision.priority

        // --- assign warehouse ---
        order.warehouseId = if (order.priority) "WH-PRIORITY" else "WH-STANDARD"

        // --- save ---
        order.status = OrderStatus.CONFIRMED
        return orderRepository.save(order)
    }
}

