package com.loopers.domain.point

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal

@Entity
@Table(name = "point")
class PointEntity(
    userId: String,
    amount: BigDecimal = BigDecimal.ZERO,
) : BaseEntity() {
    @Column(name = "user_id", unique = true, nullable = false)
    var userId = userId
        private set

    @Column(name = "amount", nullable = false)
    var amount = amount
        private set

    @Version
    var version: Long? = null

    fun charge(amount: BigDecimal) {
        this.amount += amount
    }

    fun use(amount: BigDecimal) {
        require(this.amount >= amount) { throw CoreException(ErrorType.NOT_ENOUGH_POINTS, "포인트가 부족합니다. 결제액 : $amount, 포인트 : $this.amount") }
        this.amount -= amount
    }

    companion object {
        fun of(command: PointCommand.ChargeInput): PointEntity {
            return PointEntity(
                userId = command.userId,
                amount = command.amount,
            )
        }
    }
}
