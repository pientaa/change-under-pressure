package com.legacydemo.refactored.checkout

import com.legacydemo.refactored.checkout.acl.CheckoutToPricingCommandMapper
import com.legacydemo.refactored.checkout.acl.CheckoutToPriorityInputMapper
import com.legacydemo.shared.customer.CustomerApi
import com.legacydemo.refactored.fulfillment.FulfillmentApi
import com.legacydemo.refactored.pricing.PricingApi
import com.legacydemo.shared.OrderStatus

/**
 * Decoupled orchestrator — only coordinates, never contains domain logic.
 *
 * Compare with the legacy version:
 * @see com.legacydemo.legacy.PlaceOrderService
 */
class PlaceOrderService(
    private val customerApi: CustomerApi,
    private val pricingApi: PricingApi,
    private val fulfillmentApi: FulfillmentApi,
    private val checkoutRepo: CheckoutOrderRepository
) {

    fun placeOrder(order: CheckoutOrder): PlaceOrderResult {
        val profile = customerApi.findProfile(order.customerId)

        val pricingCommand = CheckoutToPricingCommandMapper.map(order)
        val pricingResult = pricingApi.calculate(pricingCommand)

        val priorityInput = CheckoutToPriorityInputMapper.map(order, profile, pricingResult)
        val decision = fulfillmentApi.determinePriority(priorityInput)

        order.status = OrderStatus.CONFIRMED
        checkoutRepo.save(order)

        return PlaceOrderResult(
            orderId = order.id,
            status = order.status,
            customer = profile,
            pricing = pricingResult,
            fulfillment = decision
        )
    }
}
