package com.legacydemo.legacy

import com.legacydemo.shared.campaign.CampaignCatalog
import com.legacydemo.shared.customer.CustomerProfile
import com.legacydemo.shared.customer.CustomerProfileService
import com.legacydemo.shared.customer.Segment
import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Characterization tests for the legacy PlaceOrderService.
 *
 * These tests pin the current behavior — they serve as a safety net
 * before any refactoring. The refactored version must produce identical results.
 *
 * @see com.legacydemo.refactored.checkout.PlaceOrderServiceTest
 */
class LegacyPlaceOrderServiceTest : FunSpec({

    val vipProfile = CustomerProfile("C-VIP", vip = true, segment = Segment.GOLD)
    val regularProfile = CustomerProfile("C-REG", vip = false, segment = Segment.STANDARD)
    val goldProfile = CustomerProfile("C-GOLD", vip = false, segment = Segment.GOLD)

    fun buildService(vararg profiles: CustomerProfile): PlaceOrderService {
        val profileMap = profiles.associateBy { it.customerId }
        return PlaceOrderService(
            customerProfileService = CustomerProfileService(profileMap),
            campaignCatalog = CampaignCatalog.withDefaults(),
            orderRepository = OrderRepository()
        )
    }

    // slide:demo-02-char-test:start
    test("VIP with partner campaign gets priority routing and discount") {
        val service = buildService(vipProfile)
        val order = Order(
            id = "O-1", customerId = "C-VIP",
            campaignCode = "PARTNER2026",
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )

        val result = service.placeOrder(order)

        result.priority shouldBe true
        result.vip shouldBe true
        result.finalPrice shouldBe Money("180.00")
    }
    // slide:demo-02-char-test:end

    test("VIP with economy shipping still gets priority — VIP overrides shipping") {
        val service = buildService(vipProfile)
        val order = Order(
            id = "O-2", customerId = "C-VIP",
            campaignCode = "PARTNER2026",
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.ECONOMY
        )

        val result = service.placeOrder(order)

        result.priority shouldBe true
    }

    test("regular customer with high-value express order gets priority") {
        val service = buildService(regularProfile)
        val order = Order(
            id = "O-3", customerId = "C-REG",
            campaignCode = "PARTNER2026",
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )

        val result = service.placeOrder(order)

        result.priority shouldBe true
        result.vip shouldBe false
        result.finalPrice shouldBe Money("180.00")
    }

    test("regular customer with low-value order gets standard routing") {
        val service = buildService(regularProfile)
        val order = Order(
            id = "O-4", customerId = "C-REG",
            campaignCode = null,
            basePrice = Money("50.00"),
            shippingMethod = ShippingMethod.STANDARD
        )

        val result = service.placeOrder(order)

        result.priority shouldBe false
        result.finalPrice shouldBe Money("50.00")
    }

    test("VIP without campaign gets priority and no discount") {
        val service = buildService(vipProfile)
        val order = Order(
            id = "O-5", customerId = "C-VIP",
            campaignCode = null,
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.STANDARD
        )

        val result = service.placeOrder(order)

        result.priority shouldBe true
        result.finalPrice shouldBe Money("200.00")
    }

    test("GOLD segment with partner campaign above gold threshold gets priority") {
        val service = buildService(goldProfile)
        val order = Order(
            id = "O-6", customerId = "C-GOLD",
            campaignCode = "PARTNER2026",
            basePrice = Money("80.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )

        val result = service.placeOrder(order)

        // 80 * 0.9 = 72, which is >= 50 gold threshold
        result.priority shouldBe true
        result.finalPrice shouldBe Money("72.00")
    }

    test("GOLD segment with partner campaign but economy shipping gets no priority") {
        val service = buildService(goldProfile)
        val order = Order(
            id = "O-7", customerId = "C-GOLD",
            campaignCode = "PARTNER2026",
            basePrice = Money("80.00"),
            shippingMethod = ShippingMethod.ECONOMY
        )

        val result = service.placeOrder(order)

        result.priority shouldBe false
    }
})

