package com.legacydemo.refactored.fulfillment

class FulfillmentDecisionRepository {
    private val store = mutableMapOf<String, PriorityDecision>()

    fun save(orderId: String, decision: PriorityDecision): PriorityDecision {
        store[orderId] = decision
        return decision
    }

    fun findByOrderId(orderId: String): PriorityDecision? = store[orderId]
}
