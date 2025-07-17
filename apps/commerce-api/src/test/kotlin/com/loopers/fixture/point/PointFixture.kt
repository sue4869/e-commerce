package com.loopers.fixture.point

import com.loopers.domain.point.PointCommand
import com.loopers.domain.point.PointEntity
import com.loopers.interfaces.api.user.UserV1Dto
import java.math.BigDecimal

sealed class PointFixture {

    abstract val userId: String
    abstract val amount: Long

    fun pointCommand(
        userId: String = this.userId,
        amount: Long = this.amount,
    ): PointCommand.PointInfo {
        return PointCommand.PointInfo(
            userId = userId,
            amount = amount
        )
    }

    fun pointEntity(
        userId: String = this.userId,
        amount: Long = this.amount,
    ): PointEntity {
        return PointEntity(
            userId = userId,
            amount = amount
        )
    }

    object Normal: PointFixture() {
        override val userId = "user1"
        override val amount= 23L
    }
}
