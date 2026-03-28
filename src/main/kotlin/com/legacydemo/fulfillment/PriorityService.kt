package com.legacydemo.fulfillment

import com.legacydemo.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod

class PriorityService : FulfillmentApi {

    companion object {
        val PRIORITY_THRESHOLD = Money("100.00")
        val GOLD_PRIORITY_THRESHOLD = Money("50.00")
    }

    override fun determinePriority(input: PriorityInput): PriorityDecision {
        // Baseline: VIP always gets priority
        if (input.vip) {
            return PriorityDecision(priority = true)
        }

        // GOLD segment gets priority at a lower threshold (proof rule)
        if (input.segment == Segment.GOLD
            && input.campaignCode == "PARTNER2026"
            && input.finalPrice >= GOLD_PRIORITY_THRESHOLD
            && input.shippingMethod != ShippingMethod.ECONOMY
        ) {
            return PriorityDecision(priority = true)
        }

        return PriorityDecision(priority = false)
    }
}

