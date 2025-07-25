package com.loopers.domain.point

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "point")
class PointEntity(
    userId: String,
    amount: Long = 0,
) : BaseEntity() {
    @Column(name = "user_id", unique = true, nullable = false)
    var userId = userId
        private set

    @Column(name = "amount", nullable = false)
    var amount = amount
        private set

    fun updateAmount(amount: Long) {
        this.amount = amount
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
