package com.legacydemo.fulfillment

import com.legacydemo.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PriorityServiceTest : FunSpec({

    val service = PriorityService()

    test("VIP customer gets priority") {
        val input = PriorityInput(
            vip = true,
            segment = Segment.GOLD,
            campaignCode = null,
            finalPrice = Money("50.00"),
            shippingMethod = ShippingMethod.STANDARD
        )
        service.determinePriority(input).priority shouldBe true
    }

    test("non-VIP does not get priority") {
        val input = PriorityInput(
            vip = false,
            segment = Segment.STANDARD,
            campaignCode = "PARTNER2026",
            finalPrice = Money("200.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )
        service.determinePriority(input).priority shouldBe false
    }

    test("VIP + PARTNER2026 + above threshold + EXPRESS => priority") {
        val input = PriorityInput(
            vip = true,
            segment = Segment.GOLD,
            campaignCode = "PARTNER2026",
            finalPrice = Money("150.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )
        service.determinePriority(input).priority shouldBe true
    }

    test("VIP + PARTNER2026 + above threshold + ECONOMY => still priority (VIP baseline)") {
        val input = PriorityInput(
            vip = true,
            segment = Segment.GOLD,
            campaignCode = "PARTNER2026",
            finalPrice = Money("150.00"),
            shippingMethod = ShippingMethod.ECONOMY
        )
        service.determinePriority(input).priority shouldBe true
    }

    test("GOLD segment + PARTNER2026 + above gold threshold + EXPRESS => priority (lower threshold)") {
        val input = PriorityInput(
            vip = false,
            segment = Segment.GOLD,
            campaignCode = "PARTNER2026",
            finalPrice = Money("60.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )
        service.determinePriority(input).priority shouldBe true
    }

    test("GOLD segment + PARTNER2026 + below gold threshold => no priority") {
        val input = PriorityInput(
            vip = false,
            segment = Segment.GOLD,
            campaignCode = "PARTNER2026",
            finalPrice = Money("30.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )
        service.determinePriority(input).priority shouldBe false
    }

    test("GOLD segment + PARTNER2026 + above gold threshold + ECONOMY => no priority") {
        val input = PriorityInput(
            vip = false,
            segment = Segment.GOLD,
            campaignCode = "PARTNER2026",
            finalPrice = Money("60.00"),
            shippingMethod = ShippingMethod.ECONOMY
        )
        service.determinePriority(input).priority shouldBe false
    }

    test("SILVER segment + PARTNER2026 + between thresholds => no priority (standard threshold applies)") {
        val input = PriorityInput(
            vip = false,
            segment = Segment.SILVER,
            campaignCode = "PARTNER2026",
            finalPrice = Money("60.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )
        service.determinePriority(input).priority shouldBe false
    }
})

