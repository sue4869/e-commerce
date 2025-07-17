package com.loopers.domain.point

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

class PointCommand {

    data class PointInfo(
        val userId: String,
        val amount: Long,
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
        val amount: Long,
    ) {

        init {
            require(amount > 0) {
                throw CoreException(ErrorType.CHARGE_AMOUNT_MUST_BE_POSITIVE)
            }
        }

        companion object {
            fun of(userId: String, amount: Long) = ChargeInput (
                userId = userId,
                amount = amount,
            )
        }
    }
}
