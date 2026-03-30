package com.legacydemo.refactored.fulfillment

import com.legacydemo.shared.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fulfillment team's own tests — completely independent from Checkout.
 *
 * The Fulfillment team can add routing tiers, change thresholds,
 * and test everything without touching any checkout code.
 */
class PriorityServiceTest : FunSpec({

    val service = PriorityService()

    test("VIP always gets priority with next-day SLA") {
        val decision = service.determinePriority(
            PriorityInput(vip = true, segment = Segment.GOLD, campaignCode = null,
                finalPrice = Money("50.00"), shippingMethod = ShippingMethod.STANDARD)
        )
        decision.priority shouldBe true
        decision.slaTier shouldBe SlaTier.NEXT_DAY
    }

    test("VIP gets priority even with economy shipping") {
        val decision = service.determinePriority(
            PriorityInput(vip = true, segment = Segment.GOLD, campaignCode = "PARTNER2026",
                finalPrice = Money("150.00"), shippingMethod = ShippingMethod.ECONOMY)
        )
        decision.priority shouldBe true
        decision.slaTier shouldBe SlaTier.NEXT_DAY
    }

    test("GOLD + partner campaign above gold threshold gets expedited") {
        val decision = service.determinePriority(
            PriorityInput(vip = false, segment = Segment.GOLD, campaignCode = "PARTNER2026",
                finalPrice = Money("60.00"), shippingMethod = ShippingMethod.EXPRESS)
        )
        decision.priority shouldBe true
        decision.slaTier shouldBe SlaTier.EXPEDITED
    }

    test("GOLD + partner campaign below threshold falls to standard") {
        val decision = service.determinePriority(
            PriorityInput(vip = false, segment = Segment.GOLD, campaignCode = "PARTNER2026",
                finalPrice = Money("30.00"), shippingMethod = ShippingMethod.EXPRESS)
        )
        decision.priority shouldBe false
    }

    test("high-value express order gets expedited priority") {
        val decision = service.determinePriority(
            PriorityInput(vip = false, segment = Segment.STANDARD, campaignCode = null,
                finalPrice = Money("150.00"), shippingMethod = ShippingMethod.EXPRESS)
        )
        decision.priority shouldBe true
        decision.slaTier shouldBe SlaTier.EXPEDITED
    }

    test("low-value standard order gets standard routing") {
        val decision = service.determinePriority(
            PriorityInput(vip = false, segment = Segment.STANDARD, campaignCode = null,
                finalPrice = Money("50.00"), shippingMethod = ShippingMethod.STANDARD)
        )
        decision.priority shouldBe false
    }
})

