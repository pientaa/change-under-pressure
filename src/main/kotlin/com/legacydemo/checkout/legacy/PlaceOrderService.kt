package com.legacydemo.checkout.legacy

import com.legacydemo.customer.CustomerProfileService
import com.legacydemo.fulfillment.FulfillmentApi
import com.legacydemo.fulfillment.PriorityInput
import com.legacydemo.pricing.PricingApi
import com.legacydemo.pricing.PricingCommand
import com.legacydemo.repo.OrderRepository
import com.legacydemo.shared.OrderStatus

class PlaceOrderService(
    private val customerProfileService: CustomerProfileService,
    private val pricingApi: PricingApi,
    private val fulfillmentApi: FulfillmentApi,
    private val orderRepository: OrderRepository
) {

    fun placeOrder(order: Order): Order {
        // --- enrich customer info ---
        val profile = customerProfileService.findProfile(order.customerId)
        order.vip = profile.vip
        order.segment = profile.segment

        // --- calculate pricing (via Pricing Team API) ---
        val pricingCommand = PricingCommand(
            basePrice = order.basePrice,
            campaignCode = order.campaignCode
        )
        val pricingResult = pricingApi.calculate(pricingCommand)
        order.finalPrice = pricingResult.finalPrice

        // --- determine priority (via Fulfillment Team API) ---
        val priorityInput = PriorityInput(
            vip = order.vip,
            segment = order.segment,
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

