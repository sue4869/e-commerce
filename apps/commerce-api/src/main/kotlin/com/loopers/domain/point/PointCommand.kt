package com.loopers.domain.point

import java.math.BigDecimal

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
}
