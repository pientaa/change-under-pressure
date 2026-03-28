package com.legacydemo.repo

import com.legacydemo.checkout.legacy.Order

class OrderRepository {
    private val store = mutableMapOf<String, Order>()

    fun save(order: Order): Order {
        store[order.id] = order
        return order
    }

    fun findById(id: String): Order? = store[id]

    fun findAll(): List<Order> = store.values.toList()
}

