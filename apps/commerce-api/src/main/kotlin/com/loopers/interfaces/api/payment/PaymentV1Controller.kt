package com.loopers.interfaces.api.payment

import com.loopers.application.payment.PaymentFacade
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payment")
class PaymentV1Controller(
    private val paymentFacade: PaymentFacade,
) {

    @PostMapping("/after")
    fun executeAfterPg(@RequestBody request: HandlePaymentAfterRequest) {
        paymentFacade.executeAfterPg(request.toCommand())
    }
}
