package com.legacydemo

import com.legacydemo.campaign.CampaignCatalog
import com.legacydemo.checkout.legacy.Order
import com.legacydemo.checkout.legacy.PlaceOrderService
import com.legacydemo.customer.CustomerProfile
import com.legacydemo.customer.CustomerProfileService
import com.legacydemo.customer.Segment
import com.legacydemo.fulfillment.PriorityService
import com.legacydemo.pricing.PricingService
import com.legacydemo.repo.OrderRepository
import com.legacydemo.shared.Money
import com.legacydemo.shared.ShippingMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class PlaceOrderCharacterizationTest : FunSpec({

    val vipProfile = CustomerProfile("C-VIP", vip = true, segment = Segment.GOLD)
    val regularProfile = CustomerProfile("C-REG", vip = false, segment = Segment.STANDARD)

    fun buildService(vararg profiles: CustomerProfile): PlaceOrderService {
        val profileMap = profiles.associateBy { it.customerId }
        return PlaceOrderService(
            customerProfileService = CustomerProfileService(profileMap),
            pricingApi = PricingService(CampaignCatalog.withDefaults()),
            fulfillmentApi = PriorityService(),
            orderRepository = OrderRepository()
        )
    }

    test("VIP + PARTNER2026 + above threshold + EXPRESS => priority true") {
        val service = buildService(vipProfile)
        val order = Order(
            id = "O-1",
            customerId = "C-VIP",
            campaignCode = "PARTNER2026",
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )

        val result = service.placeOrder(order)

        result.priority shouldBe true
        result.vip shouldBe true
        result.finalPrice shouldBe Money("180.00") // 10% discount
        result.warehouseId shouldBe "WH-PRIORITY"
    }

    test("VIP + PARTNER2026 + above threshold + ECONOMY => priority stays from VIP baseline") {
        val service = buildService(vipProfile)
        val order = Order(
            id = "O-2",
            customerId = "C-VIP",
            campaignCode = "PARTNER2026",
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.ECONOMY
        )

        val result = service.placeOrder(order)

        // Point X does not fire (ECONOMY), but baseline VIP still sets priority
        result.priority shouldBe true
        result.warehouseId shouldBe "WH-PRIORITY"
    }

    test("non-VIP + PARTNER2026 => priority false") {
        val service = buildService(regularProfile)
        val order = Order(
            id = "O-3",
            customerId = "C-REG",
            campaignCode = "PARTNER2026",
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.EXPRESS
        )

        val result = service.placeOrder(order)

        result.priority shouldBe false
        result.vip shouldBe false
        result.finalPrice shouldBe Money("180.00")
        result.warehouseId shouldBe "WH-STANDARD"
    }

    test("VIP + no campaign => priority true (baseline VIP)") {
        val service = buildService(vipProfile)
        val order = Order(
            id = "O-4",
            customerId = "C-VIP",
            campaignCode = null,
            basePrice = Money("200.00"),
            shippingMethod = ShippingMethod.STANDARD
        )

        val result = service.placeOrder(order)

        result.priority shouldBe true
        result.finalPrice shouldBe Money("200.00") // no discount
    }
})

