package com.loopers.application.order

import com.loopers.domain.coupon.UserToCouponService
import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderItemService
import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.utils.PriceUtils
import com.loopers.domain.type.OrderStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Component
class OrderFacade(
    private val orderService: OrderService,
    private val orderItemService: OrderItemService,
    private val paymentService: PaymentService,
    private val userToCouponService: UserToCouponService,
    ) {

    @Transactional
    fun create(orderCommand: OrderCommand.Create, paymentCommand: PaymentCommand.Create) {
        orderCommand.couponId?.let { userToCouponService.validate(orderCommand.userId, orderCommand.couponId, paymentCommand) }
        //주문 생성
        val orderDto = orderService.create(orderCommand)
        val orderItems = orderItemService.create(orderCommand.items, orderDto.orderId)

        //결제 요청
        requirePayment(orderDto.uuid, orderCommand.userId, orderItems.sumOf { it.totalPrice }, paymentCommand)
        orderService.updateStatus(orderDto.uuid, OrderStatus.PAYMENT_PENDING)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun requirePayment(orderUUId: String, userId: String, reqTotalPrice: Long, paymentCommand: PaymentCommand.Create) {
        paymentService.charge(orderUUId, userId, reqTotalPrice, paymentCommand)
    }
}
