# Change Under Pressure

**Author:** [Łukasz Pięta](https://www.lukaszpieta.tech/)

---

## Domain Overview — "Place Order"

An e-commerce company evolved from one team into four:

- **Checkout Team** — orchestrates the order flow
- **Pricing & Campaigns Team** — calculates prices, applies discounts
- **Fulfillment Team** — decides priority routing and warehouse assignment
- **Customer Team** — manages customer profiles, VIP status, segments

But the **system boundaries did not evolve accordingly**:

- One shared `Order` model (a "super-model" mixing all concerns)
- One orchestration service `PlaceOrderService` (a "shared flow" embedding all rules)

### The result: rising cost of change

- "Small" changes cut across 4 areas (customer, campaign, pricing, shipping)
- Coordination tax increases with every new rule
- Regression risk grows
- Deployments become unpredictable

---

## Project Structure

The codebase is organised as **side-by-side packages** — `legacy/` vs `refactored/` — so both versions coexist and can be compared at any time.

```
src/main/kotlin/com/legacydemo/
├── legacy/                          # The "before" — monolithic code
│   ├── Order.kt                     # Super-model mixing all domains
│   ├── PlaceOrderService.kt         # God-class orchestrator
│   └── OrderRepository.kt
├── refactored/                      # The "after" — bounded contexts
│   ├── checkout/                    # Checkout context (orchestrator)
│   │   ├── CheckoutOrder.kt         # Slim, checkout-owned model
│   │   ├── PlaceOrderService.kt     # Thin orchestrator — coordinates only
│   │   ├── PlaceOrderResult.kt      # Composite result
│   │   ├── CheckoutOrderRepository.kt
│   │   └── acl/                     # Anti-Corruption Layer
│   │       ├── CheckoutToPricingCommandMapper.kt
│   │       └── CheckoutToPriorityInputMapper.kt
│   ├── fulfillment/                 # Fulfillment context
│   │   ├── FulfillmentApi.kt        # Team API + PriorityInput/PriorityDecision
│   │   ├── PriorityService.kt       # Implements FulfillmentApi
│   │   └── FulfillmentDecisionRepository.kt
│   └── pricing/                     # Pricing context
│       ├── PricingApi.kt            # Team API + PricingCommand/PricingResult
│       ├── PricingService.kt        # Implements PricingApi
│       └── PricingResultRepository.kt
└── shared/                          # Cross-cutting types
    ├── Money.kt
    ├── OrderStatus.kt
    ├── ShippingMethod.kt
    ├── campaign/
    │   └── CampaignCatalog.kt
    └── customer/
        └── CustomerProfileService.kt  # CustomerApi + CustomerProfile + Segment

src/test/kotlin/com/legacydemo/
├── legacy/
│   └── LegacyPlaceOrderServiceTest.kt    # Characterization tests (safety net)
└── refactored/
    ├── checkout/
    │   └── PlaceOrderServiceTest.kt       # Refactored orchestrator tests
    ├── fulfillment/
    │   └── PriorityServiceTest.kt         # Fulfillment-owned tests
    └── pricing/
        └── PricingServiceTest.kt          # Pricing-owned tests
```

---

## Slide-by-Slide Guide

The presentation is delivered **entirely from slides** — no IDE switching during the talk.

### Deck file

`slides/slides-slide-demo.html` — open in a browser and press **F** for fullscreen (Reveal.js).

## Build & Test

```bash
./gradlew build
./gradlew test
```

Requires **JDK 21+**. No Spring, no HTTP, no DB — everything is in-memory and deterministic.

---

## Key Takeaway

> **Bounded context ≠ microservice.**
> Draw boundaries in **code** first — contracts, models, packages.
> Deployment topology is a separate, later decision.

The goal is not a distributed system. The goal is **reducing the cost of change**
by aligning code boundaries with team responsibilities.

**Author & Contact:** [https://www.lukaszpieta.tech/](https://www.lukaszpieta.tech/)

