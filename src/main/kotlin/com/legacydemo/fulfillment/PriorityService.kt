package com.legacydemo.fulfillment

import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod

class PriorityService : FulfillmentApi {

    companion object {
        val PRIORITY_THRESHOLD = Money("100.00")
    }

    override fun determinePriority(input: PriorityInput): PriorityDecision {
        // Baseline: VIP always gets priority
        if (input.vip) {
            return PriorityDecision(priority = true)
        }

        // Point X: VIP + PARTNER2026 + high value + non-economy
        // (already covered by VIP check above, but kept explicit for clarity)
        if (input.vip
            && input.campaignCode == "PARTNER2026"
            && input.finalPrice >= PRIORITY_THRESHOLD
            && input.shippingMethod != ShippingMethod.ECONOMY
        ) {
            return PriorityDecision(priority = true)
        }

        return PriorityDecision(priority = false)
    }
}

