package com.legacydemo.refactored.pricing

import com.legacydemo.shared.campaign.Campaign
import com.legacydemo.shared.campaign.CampaignCatalog
import com.legacydemo.shared.campaign.CampaignCode
import com.legacydemo.shared.Money
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

/**
 * Pricing team's own tests — completely independent from Checkout.
 *
 * The Pricing team can add campaigns, change discount strategies,
 * and test everything without touching any checkout code.
 */
class PricingServiceTest : FunSpec({

    context("standard campaign discounts") {
        val service = PricingService(CampaignCatalog.withDefaults())

        test("known campaign applies discount and returns campaign name") {
            val result = service.calculate(
                PricingCommand(basePrice = Money("200.00"), campaignCode = "PARTNER2026")
            )
            result.finalPrice shouldBe Money("180.00")
            result.appliedDiscount shouldBe true
            result.discountName shouldBe "Partner campaign 2026"
        }

        test("no campaign code means full price") {
            val result = service.calculate(
                PricingCommand(basePrice = Money("200.00"), campaignCode = null)
            )
            result.finalPrice shouldBe Money("200.00")
            result.appliedDiscount shouldBe false
        }

        test("unknown campaign code means full price") {
            val result = service.calculate(
                PricingCommand(basePrice = Money("200.00"), campaignCode = "UNKNOWN")
            )
            result.finalPrice shouldBe Money("200.00")
            result.appliedDiscount shouldBe false
        }
    }

    context("pricing team evolves strategy without touching checkout") {
        val catalog = CampaignCatalog(
            mapOf(
                CampaignCode("PARTNER2026") to Campaign(
                    code = CampaignCode("PARTNER2026"),
                    description = "Partner campaign 2026",
                    discountPercent = BigDecimal("10")
                ),
                CampaignCode("SUMMER2026") to Campaign(
                    code = CampaignCode("SUMMER2026"),
                    description = "Summer sale 2026",
                    discountPercent = BigDecimal("25")
                )
            )
        )
        val service = PricingService(catalog)

        test("new campaign applies its own discount — checkout unchanged") {
            val result = service.calculate(
                PricingCommand(basePrice = Money("200.00"), campaignCode = "SUMMER2026")
            )
            result.finalPrice shouldBe Money("150.00")
            result.appliedDiscount shouldBe true
            result.discountName shouldBe "Summer sale 2026"
        }
    }
})

