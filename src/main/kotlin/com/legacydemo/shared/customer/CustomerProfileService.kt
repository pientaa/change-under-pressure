package com.legacydemo.shared.customer

enum class Segment {
    STANDARD, SILVER, GOLD, PLATINUM
}

data class CustomerProfile(
    val customerId: String,
    val vip: Boolean,
    val segment: Segment
)

interface CustomerApi {
    fun findProfile(customerId: String): CustomerProfile
}

class CustomerProfileService(
    private val profiles: Map<String, CustomerProfile>
) : CustomerApi {
    override fun findProfile(customerId: String): CustomerProfile =
        profiles[customerId]
            ?: CustomerProfile(customerId, vip = false, segment = Segment.STANDARD)
}

