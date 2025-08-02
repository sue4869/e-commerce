package com.loopers.domain.point

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import java.math.BigDecimal

class PointCommand {

    data class PointInfo(
        val userId: String,
        val amount: BigDecimal,
    ) {

        companion object {
            fun of(source: PointEntity): PointInfo = PointInfo (
                    userId = source.userId,
                    amount = source.amount,
                )
        }
    }

    data class ChargeInput(
        val userId: String,
        val amount: BigDecimal,
    ) {

        init {
            require(amount > BigDecimal.ZERO) {
                throw CoreException(ErrorType.CHARGE_AMOUNT_MUST_BE_POSITIVE)
            }
        }

        companion object {
            fun of(userId: String, amount: BigDecimal) = ChargeInput (
                userId = userId,
                amount = amount,
            )
        }
    }
}
