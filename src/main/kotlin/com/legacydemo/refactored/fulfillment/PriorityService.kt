package com.legacydemo.refactored.fulfillment

import com.legacydemo.shared.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod

// slide:demo-07-priority-service:start
class PriorityService : FulfillmentApi {

    companion object {
        private val PRIORITY_THRESHOLD = Money("100.00")
        private val GOLD_PRIORITY_THRESHOLD = Money("50.00")
    }

    override fun determinePriority(input: PriorityInput): PriorityDecision {
        if (input.vip) {
            return priorityRouting(SlaTier.NEXT_DAY)
        }
        if (input.segment == Segment.GOLD
            && input.campaignCode == "PARTNER2026"
            && input.finalPrice >= GOLD_PRIORITY_THRESHOLD
            && input.shippingMethod != ShippingMethod.ECONOMY
        ) {
            return priorityRouting(SlaTier.EXPEDITED)
        }
        if (input.finalPrice >= PRIORITY_THRESHOLD
            && input.shippingMethod != ShippingMethod.ECONOMY
        ) {
            return priorityRouting(SlaTier.EXPEDITED)
        }
        return standardRouting()
    }

    private fun priorityRouting(sla: SlaTier) = PriorityDecision(
        priority = true, slaTier = sla
    )

    private fun standardRouting() = PriorityDecision(
        priority = false, slaTier = SlaTier.STANDARD
    )
}
// slide:demo-07-priority-service:end
