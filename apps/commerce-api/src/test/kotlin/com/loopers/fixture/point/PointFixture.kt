package com.loopers.fixture.point

import com.loopers.domain.point.PointCommand
import com.loopers.interfaces.api.user.UserV1Dto
import java.math.BigDecimal

sealed class PointFixture {

    abstract val userId: String
    abstract val amount: BigDecimal

    fun pointCommand(
        userId: String,
        amount: BigDecimal,
    ): PointCommand.PointInfo {
        return PointCommand.PointInfo(
            userId = userId,
            amount = amount
        )
    }

    object Normal: PointFixture() {
        override val userId = "user1"
        override val amount= BigDecimal.valueOf(23L)
    }
}
