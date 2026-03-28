package com.legacydemo.customer

enum class Segment {
    STANDARD, SILVER, GOLD, PLATINUM
}

data class CustomerProfile(
    val customerId: String,
    val vip: Boolean,
    val segment: Segment
)

class CustomerProfileService(
    private val profiles: Map<String, CustomerProfile>
) {
    fun findProfile(customerId: String): CustomerProfile =
        profiles[customerId]
            ?: CustomerProfile(customerId, vip = false, segment = Segment.STANDARD)
}

