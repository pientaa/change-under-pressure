package com.legacydemo.pricing

import com.legacydemo.campaign.CampaignCatalog
import com.legacydemo.shared.Money
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PricingServiceTest : FunSpec({

    val service = PricingService(CampaignCatalog.withDefaults())

    test("PARTNER2026 applies 10% discount") {
        val result = service.calculate(
            PricingCommand(basePrice = Money("200.00"), campaignCode = "PARTNER2026")
        )

        result.finalPrice shouldBe Money("180.00")
        result.appliedDiscount shouldBe true
    }

    test("no campaign code => no discount") {
        val result = service.calculate(
            PricingCommand(basePrice = Money("200.00"), campaignCode = null)
        )

        result.finalPrice shouldBe Money("200.00")
        result.appliedDiscount shouldBe false
    }

    test("unknown campaign code => no discount") {
        val result = service.calculate(
            PricingCommand(basePrice = Money("200.00"), campaignCode = "UNKNOWN")
        )

        result.finalPrice shouldBe Money("200.00")
        result.appliedDiscount shouldBe false
    }
})

