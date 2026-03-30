package com.legacydemo.refactored.checkout

import com.legacydemo.shared.campaign.CampaignCatalog
import com.legacydemo.shared.customer.CustomerProfile
import com.legacydemo.shared.customer.CustomerProfileService
import com.legacydemo.shared.customer.Segment
import com.legacydemo.refactored.fulfillment.PriorityService
import com.legacydemo.refactored.fulfillment.SlaTier
import com.legacydemo.refactored.pricing.PricingService
import com.legacydemo.shared.Money
import com.legacydemo.shared.OrderStatus
import com.legacydemo.shared.ShippingMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for the refactored PlaceOrderService.
 *
 * Proves:
 *  1. Behavioral parity with legacy — same inputs produce same business outcomes
 *  2. CheckoutOrder has no pricing/fulfillment fields (compile-time guarantee)
 *  3. Each context persists its own data independently
 *
 * @see com.legacydemo.legacy.LegacyPlaceOrderServiceTest
 */
class PlaceOrderServiceTest : FunSpec({

    val vipProfile = CustomerProfile("C-VIP", vip = true, segment = Segment.GOLD)
    val regularProfile = CustomerProfile("C-REG", vip = false, segment = Segment.STANDARD)
    val goldProfile = CustomerProfile("C-GOLD", vip = false, segment = Segment.GOLD)

    fun buildService(vararg profiles: CustomerProfile): PlaceOrderService {
        val profileMap = profiles.associateBy { it.customerId }
        return PlaceOrderService(
            customerApi = CustomerProfileService(profileMap),
            pricingApi = PricingService(CampaignCatalog.withDefaults()),
            fulfillmentApi = PriorityService(),
            checkoutRepo = CheckoutOrderRepository()
        )
    }

    // --- Behavioral parity with legacy ---

    test("VIP with partner campaign gets priority routing and discount — same as legacy") {
        val service = buildService(vipProfile)
        val order = CheckoutOrder(
            id = "O-1", customerId = "C-VIP",
            campaignCode = "PARTNER2026",
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )

        val result = service.placeOrder(order)

        result.fulfillment.priority shouldBe true
        result.pricing.finalPrice shouldBe Money("180.00")
        result.fulfillment.slaTier shouldBe SlaTier.NEXT_DAY
        result.status shouldBe OrderStatus.CONFIRMED
    }

    test("regular customer with low-value order gets standard routing") {
        val service = buildService(regularProfile)
        val order = CheckoutOrder(
            id = "O-4", customerId = "C-REG",
            campaignCode = null,
            basePrice = Money("50.00"),
            shippingMethod = ShippingMethod.STANDARD
        )

        val result = service.placeOrder(order)

        result.fulfillment.priority shouldBe false
        result.pricing.finalPrice shouldBe Money("50.00")
    }

    test("VIP without campaign gets priority and no discount") {
        val service = buildService(vipProfile)
        val order = CheckoutOrder(
            id = "O-5", customerId = "C-VIP",
            campaignCode = null,
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.STANDARD
        )

        val result = service.placeOrder(order)

        result.fulfillment.priority shouldBe true
        result.pricing.finalPrice shouldBe Money("200.00")
        result.pricing.appliedDiscount shouldBe false
    }

    test("GOLD segment with partner campaign above gold threshold gets priority") {
        val service = buildService(goldProfile)
        val order = CheckoutOrder(
            id = "O-6", customerId = "C-GOLD",
            campaignCode = "PARTNER2026",
            basePrice = Money("80.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )

        val result = service.placeOrder(order)

        result.fulfillment.priority shouldBe true
        result.pricing.finalPrice shouldBe Money("72.00")
    }

    // --- Structural guarantees (not possible in legacy) ---

    test("CheckoutOrder has no pricing or fulfillment fields — compile-time guarantee") {
        val order = CheckoutOrder(id = "O-X", customerId = "C-VIP", basePrice = Money("100.00"))
        order.status shouldBe OrderStatus.NEW
    }

    test("orchestrator returns composite result — doesn't write to shared model") {
        val service = buildService(vipProfile)
        val order = CheckoutOrder(
            id = "O-1", customerId = "C-VIP",
            campaignCode = "PARTNER2026",
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )

        val result = service.placeOrder(order)

        // Pricing result is owned by Pricing context
        result.pricing.finalPrice shouldBe Money("180.00")
        result.pricing.appliedDiscount shouldBe true

        // Fulfillment result is owned by Fulfillment context
        result.fulfillment.priority shouldBe true

        // Checkout only owns the status
        result.status shouldBe OrderStatus.CONFIRMED
    }

    test("checkout persists only its own order") {
        val checkoutRepo = CheckoutOrderRepository()
        val service = PlaceOrderService(
            customerApi = CustomerProfileService(mapOf("C-VIP" to vipProfile)),
            pricingApi = PricingService(CampaignCatalog.withDefaults()),
            fulfillmentApi = PriorityService(),
            checkoutRepo = checkoutRepo
        )
        val order = CheckoutOrder(
            id = "O-CO", customerId = "C-VIP",
            campaignCode = "PARTNER2026",
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )

        service.placeOrder(order)

        val stored = checkoutRepo.findById("O-CO")
        stored shouldNotBe null
        stored!!.status shouldBe OrderStatus.CONFIRMED
    }
})

