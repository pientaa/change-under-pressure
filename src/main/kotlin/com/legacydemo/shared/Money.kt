package com.legacydemo.shared

import java.math.BigDecimal
import java.math.RoundingMode

data class Money(val amount: BigDecimal) {
    constructor(value: String) : this(BigDecimal(value).setScale(2, RoundingMode.HALF_UP))
    constructor(value: Int) : this(BigDecimal(value).setScale(2, RoundingMode.HALF_UP))

    operator fun times(factor: BigDecimal): Money =
        Money(amount.multiply(factor).setScale(2, RoundingMode.HALF_UP))

    operator fun minus(other: Money): Money =
        Money((amount - other.amount).setScale(2, RoundingMode.HALF_UP))

    operator fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    override fun toString(): String = "$amount"
}

