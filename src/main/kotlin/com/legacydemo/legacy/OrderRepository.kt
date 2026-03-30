package com.legacydemo.legacy

/**
 * Legacy shared repository — stores the fat Order model
 * containing data owned by all four domains.
 *
 * Compare with the refactored version where each context has its own repository:
 * @see com.legacydemo.refactored.checkout.CheckoutOrderRepository
 * @see com.legacydemo.refactored.pricing.PricingResultRepository
 * @see com.legacydemo.refactored.fulfillment.FulfillmentDecisionRepository
 */
class OrderRepository {
    private val store = mutableMapOf<String, Order>()

    fun save(order: Order): Order {
        store[order.id] = order
        return order
    }

    fun findById(id: String): Order? = store[id]

    fun findAll(): List<Order> = store.values.toList()
}
