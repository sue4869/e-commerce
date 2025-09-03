package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentEntity
import com.loopers.domain.payment.PaymentRepository
import org.springframework.stereotype.Component

@Component
class PaymentRepositoryImpl(
    private val paymentJpaRepository: PaymentJpaRepository,
) : PaymentRepository {

    override fun save(payment: PaymentEntity): PaymentEntity {
        return paymentJpaRepository.save(payment)
    }

    override fun saveAll(payments: List<PaymentEntity>): List<PaymentEntity> {
        return paymentJpaRepository.saveAll(payments)
    }

    override fun findByOrderUUId(orderUUId: String): List<PaymentEntity> {
        return paymentJpaRepository.findByOrderUUId(orderUUId)
    }
}
