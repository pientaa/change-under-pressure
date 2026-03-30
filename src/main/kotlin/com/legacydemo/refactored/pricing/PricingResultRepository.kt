package com.legacydemo.refactored.pricing

class PricingResultRepository {
    private val store = mutableMapOf<String, PricingResult>()

    fun save(orderId: String, result: PricingResult): PricingResult {
        store[orderId] = result
        return result
    }

    fun findByOrderId(orderId: String): PricingResult? = store[orderId]
}
