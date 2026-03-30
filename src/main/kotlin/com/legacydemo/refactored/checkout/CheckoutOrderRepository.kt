package com.legacydemo.refactored.checkout

class CheckoutOrderRepository {
    private val store = mutableMapOf<String, CheckoutOrder>()

    fun save(order: CheckoutOrder): CheckoutOrder {
        store[order.id] = order
        return order
    }

    fun findById(id: String): CheckoutOrder? = store[id]
}
