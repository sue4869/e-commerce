package com.loopers.fixture.point

import com.loopers.domain.point.PointCommand
import com.loopers.domain.point.PointEntity
import com.loopers.interfaces.api.point.PointV1Models
import java.math.BigDecimal

sealed class PointFixture {

    abstract val userId: String
    abstract val amount: BigDecimal

    fun pointCommand(
        userId: String = this.userId,
        amount: BigDecimal = this.amount,
    ): PointCommand.ChargeInput {
        return PointCommand.ChargeInput(
            userId = userId,
            amount = amount
        )
    }

    fun pointEntity(
        userId: String = this.userId,
        amount: BigDecimal = this.amount,
    ): PointEntity {
        return PointEntity(
            userId = userId,
            amount = amount
        )
    }

    fun chargeRequest(
        amount: BigDecimal = this.amount,
    ): PointV1Models.Request.Charge {
        return PointV1Models.Request.Charge(amount = amount)
    }

    object Normal: PointFixture() {
        override val userId = "user1"
        override val amount= BigDecimal.valueOf(23)
    }
}
