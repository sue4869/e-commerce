package com.loopers.domain.payment

interface PaymentRepository {

    fun save(payment: PaymentEntity): PaymentEntity

    fun saveAll(payments: List<PaymentEntity>): List<PaymentEntity>

    fun findByOrderUUId(orderUUId: String): List<PaymentEntity>
}
