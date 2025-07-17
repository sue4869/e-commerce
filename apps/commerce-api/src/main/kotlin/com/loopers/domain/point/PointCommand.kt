package com.loopers.domain.point

import java.math.BigDecimal

class PointCommand {

    data class PointInfo(
        val userId: String,
        val amount: BigDecimal,
    )
}
